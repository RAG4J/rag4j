package org.rag4j.integrations.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.Collections;
import java.util.List;

/**
 * OpenAI specific implementation of {@link Embedder}. The task for this component is to create an embedding for a piece
 * of text. The embedding is created using the OpenAI API.
 */
public class OpenAIEmbedder implements Embedder {
    private final OpenAIClient client;
    private final String model;

    public OpenAIEmbedder() {
        this(new KeyLoader(), OpenAIConstants.DEFAULT_EMBEDDING);
    }

    public OpenAIEmbedder(KeyLoader keyLoader) {
        this(keyLoader, OpenAIConstants.DEFAULT_EMBEDDING);
    }

    public OpenAIEmbedder(KeyLoader keyLoader, String model) {
        this.client = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        this.model = model;
    }

    public OpenAIEmbedder(OpenAIClient client) {
        this(client, OpenAIConstants.DEFAULT_EMBEDDING);
    }

    public OpenAIEmbedder(OpenAIClient client, String model) {
        this.client = client;
        this.model = model;
    }

    @Override
    public List<Double> embed(String text) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(Collections.singletonList(text));

        Embeddings embeddings = this.client.getEmbeddings(this.model, embeddingsOptions);

        return embeddings.getData().getFirst().getEmbedding();
    }
}
