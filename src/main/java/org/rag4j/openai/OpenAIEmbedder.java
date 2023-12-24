package org.rag4j.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import org.rag4j.indexing.Embedder;
import org.rag4j.util.KeyLoader;

import java.util.Collections;
import java.util.List;

import static org.rag4j.openai.OpenAIConstants.ADA2;
import static org.rag4j.openai.OpenAIFactory.obtainsClient;

/**
 * OpenAI specific implementation of {@link Embedder}. The task for this component is to create an embedding for a piece
 * of text. The embedding is created using the OpenAI API.
 */
public class OpenAIEmbedder implements Embedder {
    private final OpenAIClient client;

    public OpenAIEmbedder() {
        this(new KeyLoader());
    }

    public OpenAIEmbedder(KeyLoader keyLoader) {
        this.client = obtainsClient(keyLoader.getOpenAIKey());
    }

    @Override
    public List<Double> embed(String text) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(Collections.singletonList(text));

        Embeddings embeddings = this.client.getEmbeddings(ADA2, embeddingsOptions);

        return embeddings.getData().getFirst().getEmbedding();
    }
}
