package org.rag4j.indexing;

import org.rag4j.domain.Chunk;
import org.rag4j.domain.InputDocument;

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

    /**
     * Default implementation that splits the document into chunks using the provided {@link Splitter} and then calls
     * the method {@link #store(List)}.
     * @param document the document to split into chunks and then store
     * @param splitter the splitter to use to split the document into chunks
     */
    default void store(InputDocument document, Splitter splitter) {
        List<Chunk> chunks = splitter.split(document);
        store(chunks);
    }
}
