package org.rag4j.applications.indexing;

import java.util.List;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.splitters.SentenceSplitter;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.integrations.weaviate.indexer.WeaviateChunkIndexer;
import org.rag4j.integrations.weaviate.retrieval.WeaviateRetriever;
import org.rag4j.integrations.weaviate.store.WeaviateContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.util.keyloader.KeyLoader;

/**
 * Shows how to index documents into Weaviate. We have abstracted creating the schema for Weaviate from the actual
 * indexing. The class {@link WeaviateAccess} contains the main methods for interacting with Weaviate. The result
 * of this application is a new class in Weaviate using the Vasa content.
 */
public class AppIndexerWeaviate {

    private static void createWeaviateSchema(WeaviateAccess weaviateAccess, String collection) {
        VasaWeaviateChunkClassBuilder WeaviateChunkClassBuilder = new VasaWeaviateChunkClassBuilder();
        weaviateAccess.forceCreateClass(WeaviateChunkClassBuilder.build(collection));
        String schema = weaviateAccess.getSchemaForClass(collection);
        System.out.println(schema);
    }

    public static void main(String[] args) {
        String collection = "VasaCollection";

        // Initialize the components
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());

        Embedder embedder = new OpenAIEmbedder(openAIClient);
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);

        WeaviateChunkIndexer weaviateChunkIndexer = new WeaviateChunkIndexer(weaviateAccess);
        WeaviateContentStore contentStore = new WeaviateContentStore(weaviateChunkIndexer, embedder, collection);
        IndexingService indexingService = new IndexingService(contentStore);
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder, false, collection);

        // Use the components
        createWeaviateSchema(weaviateAccess, collection);
        indexingService.indexDocuments(new VasaContentReader(), new SentenceSplitter());
        String question = "Since when was the Vasa available for the public to visit?";
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks(question, 1);

        // Print the result
        relevantChunks.stream().findFirst().ifPresent(chunk -> {
            System.out.printf("Relevant chunk: %s%n", chunk.getText());
            System.out.printf("Score: %.4f%n", chunk.getScore());
            System.out.printf("Document: %s", chunk.getDocumentId());
        });

    }
}
