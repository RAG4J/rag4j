package org.rag4j.rag.retrieval;

import org.rag4j.rag.retrieval.strategies.TopNRetrievalStrategy;

import java.util.List;

/**
 * <p>A retrieval strategy is meant to make retrieving context from a retriever flexible. For example, a retrieval
 * strategy could be to return the top N chunks, or a window of chunks around the relevant chunks.</p>
 * <p>Each retrieve function accepts a question, and uses a retriever to obtain relevant chunks. A basis strategy
 * is the {@link TopNRetrievalStrategy} where all found relevant items are returned as context in the object
 * {@link RetrievalOutput}. Other implementations have other approaches.</p>
 */
public interface RetrievalStrategy {
    int DEFAULT_MAX_RESULTS = 4;

    default RetrievalOutput retrieve(String question) {
        return retrieve(question, DEFAULT_MAX_RESULTS);
    }

    RetrievalOutput retrieve(String question, int maxResults);

    RetrievalOutput retrieve(String question, List<Float> vector, int maxResults);

    /**
     * Some Retrieval strategies do not add anything to the observer. For example, the {@link TopNRetrievalStrategy}
     * @param question The question to retrieve context for
     * @param maxResults The maximum number of results to return
     * @param observe If we use the tracker to observe
     * @return The context
     */
    default RetrievalOutput retrieve(String question, int maxResults, boolean observe) {
        return retrieve(question, maxResults);
    }

    /**
     * Some Retrieval strategies do not add anything to the observer. For example, the {@link TopNRetrievalStrategy}
     * @param question The question to retrieve context for
     * @param vector The vector to retrieve context for
     * @param maxResults The maximum number of results to return
     * @param observe If we use the tracker to observe
     * @return The context
     */
    default RetrievalOutput retrieve(String question, List<Float> vector, int maxResults, boolean observe) {
        return retrieve(question, vector, maxResults);
    }
}
