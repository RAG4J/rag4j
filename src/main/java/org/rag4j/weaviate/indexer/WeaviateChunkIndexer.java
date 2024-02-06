package org.rag4j.weaviate.indexer;

import org.rag4j.domain.Chunk;
import org.rag4j.weaviate.WeaviateAccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.rag4j.weaviate.WeaviateContants.CLASS_NAME;

public class WeaviateChunkIndexer {
    private final WeaviateAccess weaviateAccess;

    public WeaviateChunkIndexer(WeaviateAccess weaviateAccess) {
        this.weaviateAccess = weaviateAccess;
    }

    public String indexChunk(Chunk chunk, List<Double> vector) {
        Map<String, Object> properties = new HashMap<>(chunk.getProperties());
        properties.put("documentId", chunk.getDocumentId());
        properties.put("chunkId", chunk.getChunkId());
        properties.put("totalChunks", chunk.getTotalChunks());
        properties.put("text", chunk.getText());

        Float[] floatVector = vector.stream()
                .map(Double::floatValue)
                .toArray(Float[]::new);

        if (!chunk.getProperties().isEmpty()) {
            properties.putAll(chunk.getProperties());
        }

        String documentId = UUID.randomUUID().toString();

        weaviateAccess.addDocument(CLASS_NAME, documentId, properties, floatVector);

        return documentId;
    }
}
