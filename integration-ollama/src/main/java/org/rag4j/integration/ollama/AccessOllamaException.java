package org.rag4j.integration.ollama;

public class AccessOllamaException extends RuntimeException {
    public AccessOllamaException(String message) {
        super(message);
    }

    public AccessOllamaException(String message, Throwable cause) {
        super(message, cause);
    }
}
