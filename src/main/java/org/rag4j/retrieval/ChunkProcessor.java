package org.rag4j.retrieval;

import org.rag4j.domain.Chunk;

public interface ChunkProcessor {
    void process(Chunk chunk);
}
