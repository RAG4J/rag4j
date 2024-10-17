package org.rag4j.util.keyloader;

import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Properties;

/**
 * This class is used to load required keys to connect to OpenAI and Weaviate. The keys are obtained from environment
 * variables. If the environment variables are not available, the keys are obtained from a properties file. The
 * properties file can be on your path, or it can be loaded from a remote location. The properties file is encrypted if
 * it is loaded from a remote location. The key for the encryption is obtained from the environment variable SECRET_KEY.
 */
public class KeyLoader {
    private final static String WEAVIATE_API_KEY = "weaviate_api_key";
    private final static String WEAVIATE_URL = "weaviate_url";
    private final static String OPENAI_API_KEY = "openai_api_key";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(KeyLoader.class);
    private final String secretKey;

    private Properties encryptedProperties;
    private final EnvironmentVariableProvider provider;

    public KeyLoader() {
        this("properties-encrypted.txt", new EnvironmentVariableProvider());
    }

    KeyLoader(String fileName, EnvironmentVariableProvider provider) {
        this.provider = provider;
        this.secretKey = provider.getEnv("SECRET_KEY");
        try {
            this.encryptedProperties = loadProperties("https://cocoen.nl/rag4jp/" + fileName);
        } catch (KeyLoaderException e) {
            LOGGER.error("There was a problem while loading the properties file from the remote location. Make sure to" +
                    "provide the environment variables yourself.");
        }
    }

    /**
     * Use this method to obtain the OpenAI API key. The key is obtained from the OPENAI_API_KEY environment variable.
     * If that is not availalbe, the key is obtained from a properties file. The properties file is loaded from a
     * remote location. This is done to prevent the key from being stored in the repository.
     * @return The OpenAI API key
     */
    public String getOpenAIKey() {
        return getProperty(OPENAI_API_KEY);
    }

    public String getWeaviateAPIKey() {
        return getProperty(WEAVIATE_API_KEY);
    }

    public String getWeaviateURL() {
        return getProperty(WEAVIATE_URL);
    }

    private String getProperty(String key) {
        String value = provider.getEnv(key);
        if (value == null) {
            LOGGER.info("Using {} key from properties loaded from remote location", key);
            if (this.secretKey == null || this.secretKey.isEmpty()) {
                throw new KeyLoaderException("The secret key is not set, therefore we cannot load from remote " +
                        "location. Make sure to set the SECRET_KEY environment variable.");
            }
            value = new String(decrypt(encryptedProperties.getProperty(key), this.secretKey), StandardCharsets.UTF_8);
        }
        return value;
    }

    private static Properties loadProperties(String url) {
        Properties properties = new Properties();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8));
            properties.load(reader);

            properties.stringPropertyNames().forEach(key -> {
                String value = properties.getProperty(key);
                value = value.replace("\\", "");
                properties.setProperty(key, value);
            });
        } catch (IOException | InterruptedException e) {
            throw new KeyLoaderException("There was a problem while loading the properties file: " + e.getMessage());
        }
        return properties;
    }

    static byte[] encrypt(String strToEncrypt, String secret) {
        try {
            Key secretKey = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encode(bytes);
        } catch (Exception e) {
            LOGGER.error("There was a problem while encrypting the key ", e);
            throw new KeyLoaderException("There was a problem while encrypting the key: " + e.getMessage());
        }
    }

    static byte[] decrypt(String strToDecrypt, String secret) {
        try {
            byte[] decodedString = Base64.getDecoder().decode(strToDecrypt);
            Key secretKey = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(decodedString);
        } catch (Exception e) {
            LOGGER.error("There was a problem while decrypting the key", e);
            throw new KeyLoaderException("There was a problem while decrypting the key: " + e.getMessage());
        }
    }

    private static void writeProperties(String url, Properties properties) {
        try (OutputStream output = new FileOutputStream(url)) {
            Writer writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
            properties.store(writer, "Keys neaded by the rag4j application");
        } catch (IOException e) {
            throw new KeyLoaderException("There was a problem while writing to the properties file: " + e.getMessage());
        }
    }

    /**
     * This runner can be use to generate the encrypted properties file. It is not used in the application itself.
     * @param args The arguments are not used
     */
    public static void main(String[] args) {
        String secretKey = "thisisjustatestkeythatweneednow9";
        String inputFile = "./properties-test.txt";
        String outputFile = "./properties-encrypted-test.txt";

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(inputFile)) {
            properties.load(input);
        } catch (IOException e) {
            LOGGER.error("There was a problem while loading the properties file: " + e.getMessage());
            throw new KeyLoaderException("There was a problem while loading the properties file: " + e.getMessage());
        }

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            byte[] encryptedValue = KeyLoader.encrypt(value, secretKey);

            properties.setProperty(key, new String(encryptedValue, StandardCharsets.UTF_8));
        }
        properties.forEach((key, value) -> System.out.println(key + ": " + value));

        writeProperties(outputFile, properties);
    }
}
