package org.rag4j.indexing;

import org.rag4j.rag.model.Chunk;

import java.util.List;

/**
 * Component used to split an {@link InputDocument} into {@link Chunk}s. If a parent chunk is provided, the splitter
 * acts on the text of the parent chunk.
 */
public interface Splitter {
    default List<Chunk> split(InputDocument inputDocument) {
        return this.split(inputDocument, null);
    }

    List<Chunk> split(InputDocument inputDocument, Chunk parentChunk);
}
