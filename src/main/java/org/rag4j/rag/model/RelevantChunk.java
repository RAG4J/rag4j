package org.rag4j.rag.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * Value object of type {@link Chunk} with an additional score meant to indicate the relevance to a question.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RelevantChunk extends Chunk {
    private final double score;

    public RelevantChunk(String documentId, String chunkId, int totalChunks, double score, String text,
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
