package org.rag4j.util.keyloader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnvironmentVariableProviderTest {

    @Test
    void shouldReturnPropertyWhenSystemEnvironmentVariableDoesNotExist() {
        EnvironmentVariableProvider provider = new EnvironmentVariableProvider();

        String result = provider.getEnv("SECRET_KEY");

        assertEquals("thisisjustatestkeythatweneednow9", result);
    }
}