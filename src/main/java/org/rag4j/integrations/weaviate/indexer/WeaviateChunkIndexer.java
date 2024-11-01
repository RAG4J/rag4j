package org.rag4j.integrations.weaviate.indexer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.rag.model.Chunk;

public class WeaviateChunkIndexer {
    private final WeaviateAccess weaviateAccess;

    public WeaviateChunkIndexer(WeaviateAccess weaviateAccess) {
        this.weaviateAccess = weaviateAccess;
    }

    public String indexChunk(Chunk chunk, List<Float> vector, String collection) {
        Map<String, Object> properties = new HashMap<>(chunk.getProperties());
        properties.put("documentId", chunk.getDocumentId());
        properties.put("chunkId", chunk.getChunkId());
        properties.put("totalChunks", chunk.getTotalChunks());
        properties.put("text", chunk.getText());

        Float[] floatVector = vector.toArray(new Float[0]);

        if (!chunk.getProperties().isEmpty()) {
            properties.putAll(chunk.getProperties());
        }

        String documentId = UUID.randomUUID().toString();

        weaviateAccess.addDocument(collection, documentId, properties, floatVector);

        return documentId;
    }
}
