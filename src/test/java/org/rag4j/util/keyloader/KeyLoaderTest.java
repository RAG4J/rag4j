package org.rag4j.util.keyloader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyLoaderTest {
    public static final String TEST_SECRET_KEY = "thisisjustatestkeythatweneednow9";

    private HttpClient client;

    @BeforeEach
    public void setup() {
        client = mock(HttpClient.class);
    }

    @Test
    @DisplayName("Should return OpenAI key from environment variable")
    public void shouldReturnOpenAIKeyFromEnvironmentVariable() {
        EnvironmentVariableProvider envProvider = mock(EnvironmentVariableProvider.class);
        when(envProvider.getEnv("openai_api_key")).thenReturn("testKey");

        KeyLoader keyLoader = new KeyLoader("properties-encrypted-test.txt", envProvider);
        assertEquals("testKey", keyLoader.getOpenAIKey());
    }

    @Test
    @DisplayName("Should return OpenAI key from properties file")
    public void shouldReturnOpenAIKeyFromPropertiesFile() {
        EnvironmentVariableProvider envProvider = mock(EnvironmentVariableProvider.class);
        when(envProvider.getEnv("SECRET_KEY")).thenReturn(TEST_SECRET_KEY);

        KeyLoader keyLoader = new KeyLoader("properties-encrypted-test.txt", envProvider);
        assertEquals("test-key-openai", keyLoader.getOpenAIKey());
    }

    @Test
    @DisplayName("Should throw KeyLoaderException when properties file is not available")
    public void shouldThrowKeyLoaderExceptionWhenPropertiesFileNotAvailable() throws IOException, InterruptedException {
        //noinspection unchecked
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        KeyLoader keyLoader =
                new KeyLoader("properties-encrypted-unknown.txt", new EnvironmentVariableProvider());

        assertThrows(KeyLoaderException.class, keyLoader::getOpenAIKey);
    }

    @Test
    @DisplayName("Should pass the encryption decryption without any steps between")
    public void shouldPassEncryptionDecryptionWithoutAnyStepsBetween() {
        String inputString = "rag4j=1u1lhda6.weaviate.network";
        byte[] encrypted = KeyLoader.encrypt(inputString, TEST_SECRET_KEY);
        byte[] decrypted = KeyLoader.decrypt(new String(encrypted, StandardCharsets.UTF_8), TEST_SECRET_KEY);
        assertEquals(inputString, new String(decrypted, StandardCharsets.UTF_8));
    }
}