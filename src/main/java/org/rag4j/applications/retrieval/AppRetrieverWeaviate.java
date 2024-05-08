package org.rag4j.applications.retrieval;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.util.keyloader.KeyLoader;
import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.integrations.weaviate.retrieval.WeaviateRetriever;

import java.util.List;

/**
 * The goal of this application is to show how to interact with Weaviate. All retrieval methods are shown in this
 * application.
 */
public class AppRetrieverWeaviate {
    public static void main(String[] args) {
        // Initialize the components
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Embedder embedder = new OpenAIEmbedder(openAIClient);

        List<String> additionalFields = List.of("title", "timerange");
        boolean useHybridSearch = false;
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder, useHybridSearch, additionalFields);

        // Use the components - Loop over all chunks
        retriever.loopOverChunks(chunk -> System.out.printf("Document: %s - %d%n", chunk.getDocumentId(), chunk.getChunkId()));
        System.out.println("-----------------");

        // Use the components - Get a specific chunk
        Chunk chunk = retriever.getChunk("the-perfect-scapegoat", 0);
        System.out.printf("Document: %s - %d%n", chunk.getDocumentId(), chunk.getChunkId());

        // Use the components - Find relevant chunks
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks("How many bolts were replaced?", 4);
        System.out.printf("Found %d relevant chunks%n", relevantChunks.size());

        for (RelevantChunk relevantChunk : relevantChunks) {
            System.out.printf("Document: %s - %d%n", relevantChunk.getDocumentId(), relevantChunk.getChunkId());
            System.out.printf("Title: %s%n", relevantChunk.getProperties().get("title"));
            System.out.println(relevantChunk.getText());
            System.out.println("-----------------");
        }
    }

}
