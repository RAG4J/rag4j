package org.rag4j.rag.model;

import lombok.*;

import java.util.Map;

/**
 * Value object representing a chunk of text. The chunk of text comes from a document with a specific id. Usually a
 * document is split into multiple chunks. Each chunk has a unique id as a sequence number and the total number of
 * chunks in the document is also provided.
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@EqualsAndHashCode
public class Chunk {
    private String documentId;
    private String chunkId;
    private int totalChunks;
    private String text;
    private Map<String,Object> properties;

    public Chunk() {
    }
}
