package org.rag4j.examples;

import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.OpenNLPSentenceSplitter;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.weaviate.indexer.WeaviateChunkClassBuilder;
import org.rag4j.weaviate.indexer.WeaviateChunkIndexer;
import org.rag4j.weaviate.indexer.WeaviateContentStore;

/**
 * Shows how to index documents into Weaviate. We have abstracted creating the schema for Weaviate from the actual
 * indexing. The class {@link WeaviateAccess} contains the main methods for interacting with Weaviate.
 */
public class AppIndexerWeaviate {

    private static void createWeaviateSchema(WeaviateAccess weaviateAccess) {
        weaviateAccess.forceCreateClass(WeaviateChunkClassBuilder.build());
        String schema = weaviateAccess.getSchemaForClass("Chunk");
        System.out.println(schema);
    }

    public static void main(String[] args) {
        // Load the different api keys from the environment
        KeyLoader keyLoader = new KeyLoader();

        // initialize Weaviate
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        createWeaviateSchema(weaviateAccess);

        // initialize the content store
        WeaviateContentStore contentStore = new WeaviateContentStore(
                new WeaviateChunkIndexer(weaviateAccess),
                new OpenAIEmbedder(keyLoader)
        );

        // index the documents using the Vasa reader and the sentence splitter
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new OpenNLPSentenceSplitter());
    }
}
