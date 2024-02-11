package org.rag4j.util.keyloader;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main purpose is to make the code better testable. Using this class enables us to mock the environment variables.
 */
public class EnvironmentVariableProvider {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EnvironmentVariableProvider.class);
    private final Properties props = new Properties();

    public EnvironmentVariableProvider() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("env.properties")) {
            if (input == null) {
                LOGGER.info("Sorry, unable to find env.properties");
                return;
            }

            //load a properties file from class path
            props.load(input);

        } catch (IOException ex) {
            LOGGER.error("There was a problem loading the env.properties file", ex);
        }
    }

    public String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = props.getProperty(name.toLowerCase());
        }
        return value;
    }
}