package org.rag4j.applications.indexing;

import org.rag4j.indexing.ContentReader;
import org.rag4j.indexing.Splitter;
import org.rag4j.integration.ollama.OllamaAccess;
import org.rag4j.integration.ollama.OllamaEmbedder;
import org.rag4j.local.store.InternalContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.splitters.SingleChunkSplitter;
import org.rag4j.rag.embedding.local.OnnxBertEmbedder;

import java.nio.file.Path;
import java.util.List;

/**
 * The Goal is to demonstrate how to use Rag4j to index documents.
 */
public class AppIndexerInMemory {
    public static void main(String[] args) {
        // Initialize the components
        Embedder embedder = new OnnxBertEmbedder();
        Splitter splitter = new SingleChunkSplitter();
        ContentReader contentReader = new VasaContentReader();

        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);

        // Use the components
        indexingService.indexDocuments(contentReader, splitter);
        contentStore.backupToDisk(Path.of("backups"), "backup.dat");

        String question = "Since when was the Vasa available for the public to visit?";
        List<RelevantChunk> relevantChunks = contentStore.findRelevantChunks(question, 1);

        // Print the result
        relevantChunks.stream().findFirst().ifPresent(chunk -> {
            System.out.printf("Relevant chunk: %s%n", chunk.getText());
            System.out.printf("Score: %.4f%n", chunk.getScore());
            System.out.printf("Document: %s%n", chunk.getDocumentId());
        });

        InternalContentStore loadedContentStore = new InternalContentStore(new OllamaEmbedder(new OllamaAccess()));
        loadedContentStore.loadFromDisk(Path.of("backups"), "backup.dat");

        relevantChunks = contentStore.findRelevantChunks(question, 1);

        // Print the result
        relevantChunks.stream().findFirst().ifPresent(chunk -> {
            System.out.printf("Relevant chunk: %s%n", chunk.getText());
            System.out.printf("Score: %.4f%n", chunk.getScore());
            System.out.printf("Document: %s", chunk.getDocumentId());
        });

    }
}
