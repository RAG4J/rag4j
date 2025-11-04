package org.rag4j.rag.retrieval;

import org.rag4j.rag.model.Chunk;

public interface ChunkProcessor {
    void process(Chunk chunk);
}
