package org.rag4j.domain;

import lombok.Getter;
import org.rag4j.domain.Chunk;

import java.util.Map;

/**
 * Value object of type {@link Chunk} with an additional score meant to indicate the relevance to a question.
 */
@Getter
public class RelevantChunk extends Chunk {
    private final double score;

    public RelevantChunk(String documentId, int chunkId, int totalChunks, double score, String text,
                         Map<String,Object> properties) {
        super(documentId, chunkId, totalChunks, text, properties);
        this.score = score;
    }

    public RelevantChunk(Chunk chunk, double score) {
        super(chunk.getDocumentId(), chunk.getChunkId(), chunk.getTotalChunks(), chunk.getText(), chunk.getProperties());
        this.score = score;
    }

    public String getDocumentChunkId() {
        return getDocumentId() + "_" + getChunkId();
    }
}
