package org.rag4j.rag.retrieval;

import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;

import java.util.List;

/**
 * This interface is used to retrieve relevant chunks for a given question. Next to the functions to retrieve the
 * answer using the question or the vector representation of the question, it also provides a function to loop over
 * all chunks. Finally it contains a method to get a specific chunk.
 */
public interface Retriever {
    List<RelevantChunk> findRelevantChunks(String question, int maxResults);
    List<RelevantChunk> findRelevantChunks(String question, List<Float> vector, int maxResults);

    Chunk getChunk(String documentId, String chunkId);

    void loopOverChunks(ChunkProcessor chunkProcessor);
}
