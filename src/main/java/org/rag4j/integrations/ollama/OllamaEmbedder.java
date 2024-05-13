package org.rag4j.integrations.ollama;

import org.rag4j.rag.embedding.Embedder;

import java.util.List;

public class OllamaEmbedder implements Embedder {
    private final OllamaAccess ollama;
    private final String embeddingModel;

    public OllamaEmbedder(OllamaAccess ollama) {
        this(ollama, OllamaConstants.DEFAULT_EMBEDDING_MODEL);

    }

    public OllamaEmbedder(OllamaAccess ollama, String embeddingModel) {
        this.ollama = ollama;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public List<Float> embed(String text) {
        return this.ollama.generateEmbedding(text, this.embeddingModel);
    }
}
