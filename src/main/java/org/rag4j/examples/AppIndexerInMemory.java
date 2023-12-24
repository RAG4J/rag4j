package org.rag4j.examples;

import org.rag4j.domain.RelevantChunk;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.SingleChunkSplitter;
import org.rag4j.localembedder.AllMiniLmL6V2QEmbedder;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.store.InternalContentStore;

import java.util.List;

/**
 * Goals is to demonstrate how to use rag4j to index and retrieve documents.
 */
public class AppIndexerInMemory {
    public static void main(String[] args) {
        InternalContentStore contentStore = new InternalContentStore(new AllMiniLmL6V2QEmbedder());
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new SingleChunkSplitter());

        String question = "Since when was the Vasa available for the public to visit?";
        List<RelevantChunk> relevantChunks = contentStore.findRelevantChunks(question, 1);
        relevantChunks.stream().findFirst().ifPresent(chunk -> {
            System.out.printf("Relevant chunk: %s%n", chunk.getText());
            System.out.printf("Score: %.4f%n", chunk.getScore());
            System.out.printf("Document: %s", chunk.getDocumentId());
        });
    }
}
