package org.rag4j.integrations.ollama;

public interface OllamaConstants {
    String EMBEDDING_MODEL_NOMIC = "nomic-embed-text";
    String EMBEDDING_MODEL_MINILM = "all-minilm";

    String MODEL_PHI3 = "phi3";
    String MODEL_LLAMA3 = "llama3";

    String DEFAULT_MODEL = MODEL_PHI3;
    String DEFAULT_EMBEDDING_MODEL = EMBEDDING_MODEL_NOMIC;

}
