package org.rag4j.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * Value object representing a chunk of text. The chunk of text comes from a document with a specific id. Usually a
 * document is split into multiple chunks. Each chunk has a unique id as a sequence number and the total number of
 * chunks in the document is also provided.
 */
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class Chunk {
    private String documentId;
    private int chunkId;
    private int totalChunks;
    private String text;
    private Map<String,Object> properties;

    public Chunk() {
    }
}
