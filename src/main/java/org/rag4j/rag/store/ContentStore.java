package org.rag4j.rag.store;

import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.model.Chunk;

import java.util.List;

/**
 * Component used store the {@link Chunk}s of an {@link InputDocument} in a persistent storage.
 */
public interface ContentStore {
    /**
     * Stores the provided {@link Chunk}s in a persistent storage.
     * @param chunks the chunks to store
     */
    void store(List<Chunk> chunks);
}
