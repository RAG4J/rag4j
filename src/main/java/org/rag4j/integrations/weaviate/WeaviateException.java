package org.rag4j.integrations.weaviate;

public class WeaviateException extends RuntimeException {
    public WeaviateException(String message) {
        super(message);
    }
}
