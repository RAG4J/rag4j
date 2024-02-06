package org.rag4j.examples;

import org.rag4j.domain.Chunk;
import org.rag4j.domain.RelevantChunk;
import org.rag4j.indexing.Embedder;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.retrieval.Retriever;
import org.rag4j.weaviate.retrieval.WeaviateRetriever;

import java.util.List;

public class AppRetrieverWeaviate {
    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Embedder embedder = new OpenAIEmbedder(keyLoader);
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder, true, List.of("title", "timerange"));

        retriever.loopOverChunks(chunk -> System.out.printf("Document: %s - %d%n", chunk.getDocumentId(), chunk.getChunkId()));

        System.out.println("-----------------");

        Chunk chunk = retriever.getChunk("the-perfect-scapegoat", 0);
        System.out.printf("Document: %s - %d%n", chunk.getDocumentId(), chunk.getChunkId());

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
