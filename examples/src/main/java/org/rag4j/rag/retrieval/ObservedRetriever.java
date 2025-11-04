package org.rag4j.rag.retrieval;

import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.tracker.RAGTracker;

import java.util.List;

/**
 * This class is used to observe the retriever and store the relevant chunks into the RAGObserver.
 */
public class ObservedRetriever implements Retriever {
    private final Retriever retriever;

    public ObservedRetriever(Retriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, int maxResults) {
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks(question, maxResults);

        relevantChunks.forEach(relevantChunk -> RAGTracker.addRelevantChunks(relevantChunk.getDocumentChunkId(), relevantChunk.getText()));
        return relevantChunks;
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, List<Float> vector, int maxResults) {
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks(question, vector, maxResults);

        relevantChunks.forEach(relevantChunk -> RAGTracker.addRelevantChunks(relevantChunk.getDocumentChunkId(), relevantChunk.getText()));
        return relevantChunks;
    }

    @Override
    public Chunk getChunk(String documentId, String chunkId) {
        return retriever.getChunk(documentId, chunkId);
    }

    @Override
    public void loopOverChunks(ChunkProcessor chunkProcessor) {
        retriever.loopOverChunks(chunkProcessor);
    }
}
