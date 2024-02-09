package org.rag4j.integrations.weaviate.store;

import org.rag4j.integrations.weaviate.indexer.WeaviateChunkIndexer;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.store.ContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.slf4j.Logger;

import java.util.List;

public class WeaviateContentStore implements ContentStore {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WeaviateContentStore.class);

    private final WeaviateChunkIndexer weaviateChunkIndexer;
    private final Embedder embedder;

    public WeaviateContentStore(WeaviateChunkIndexer weaviateChunkIndexer, Embedder embedder) {
        this.weaviateChunkIndexer = weaviateChunkIndexer;
        this.embedder = embedder;
    }

    @Override
    public void store(List<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            String documentId = weaviateChunkIndexer.indexChunk(chunk, embedder.embed(chunk.getText()));
            LOGGER.info("Indexed chunk {} with documentId {}", chunk, documentId);
        }
    }

}
