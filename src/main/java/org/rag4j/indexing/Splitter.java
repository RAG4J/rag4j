package org.rag4j.indexing;

import org.rag4j.domain.Chunk;
import org.rag4j.domain.InputDocument;

import java.util.List;

/**
 * Component used to split an {@link InputDocument} into {@link Chunk}s.
 */
public interface Splitter {
    List<Chunk> split(InputDocument inputDocument);
}
