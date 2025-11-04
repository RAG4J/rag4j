package org.rag4j.indexing;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * Value object representing a document to be used for indexing. A document is a piece of text with a unique id. A
 * document can have additional properties that can be used for filtering or scoring. A document is split into one
 * or multiple Chunks.
 */
@Data
@Builder
@EqualsAndHashCode
public class InputDocument {
    private String documentId;
    private String text;
    private Map<String,Object> properties;
}
