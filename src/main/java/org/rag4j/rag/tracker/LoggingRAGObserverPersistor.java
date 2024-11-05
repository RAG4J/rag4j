package org.rag4j.rag.tracker;

import org.slf4j.Logger;

public class LoggingRAGObserverPersistor implements RAGObserverPersistor {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoggingRAGObserverPersistor.class);

    @Override
    public void persistCommonProperties(RAGObserver ragObserver) {
        logger.info("Question: {}", ragObserver.getQuestion());
        logger.info("Context: {}", ragObserver.getContext());
        logger.info("Answer: {}", ragObserver.getAnswer());
        logger.info("Relevant chunks:");
        ragObserver.getRelevantChunks().forEach((documentChunkId, text) ->
                logger.info("  {}: {}", documentChunkId, text)
        );
    }

    @Override
    public void persistWindowProperties(RAGObserver ragObserver) {
        logger.info("Window to chunkIds:");
        ragObserver.getWindowToChunkIds().forEach((window, chunkIds) ->
                logger.info("  {}: {}", window, chunkIds)
        );
        logger.info("Window texts:");
        ragObserver.getWindowTexts().forEach((window, text) ->
                logger.info("  {}: {}", window, text)
        );
    }
}
