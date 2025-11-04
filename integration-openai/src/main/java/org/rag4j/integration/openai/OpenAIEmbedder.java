package org.rag4j.integration.openai;

import com.openai.client.OpenAIClient;
import com.openai.models.embeddings.CreateEmbeddingResponse;
import com.openai.models.embeddings.EmbeddingCreateParams;
import org.rag4j.rag.embedding.Embedder;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * OpenAI specific implementation of {@link Embedder}. The task for this component is to create an embedding for a piece
 * of text. The embedding is created using the OpenAI API.
 */
public class OpenAIEmbedder implements Embedder {
    private final OpenAIClient client;
    private final String model;

    public OpenAIEmbedder(OpenAIClient client) {
        this(client, OpenAIConstants.DEFAULT_EMBEDDING);
    }

    public OpenAIEmbedder(OpenAIClient client, String model) {
        this.client = client;
        this.model = model;
    }

    @Override
    public List<Float> embed(String text) {
        EmbeddingCreateParams build = EmbeddingCreateParams.builder()
                .input(text)
                .model(this.model)
                .encodingFormat(EmbeddingCreateParams.EncodingFormat.FLOAT)
                .build();
        CreateEmbeddingResponse createEmbeddingResponse = this.client.embeddings().create(build);

        return createEmbeddingResponse.data().getFirst().embedding();
    }

    @Override
    public String identifier() {
        return (supplier() + "-embedder-" + model()).toLowerCase();
    }

    @Override
    public String supplier() {
        return "OpenAI";
    }

    @Override
    public String model() {
        return this.model;
    }
}
