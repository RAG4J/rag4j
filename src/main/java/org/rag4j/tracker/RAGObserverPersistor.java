package org.rag4j.tracker;

public interface RAGObserverPersistor {
    default void persist(RAGObserver ragObserver) {
        persistCommonProperties(ragObserver);
        persistWindowProperties(ragObserver);
    }

    void persistCommonProperties(RAGObserver ragObserver);
    void persistWindowProperties(RAGObserver ragObserver);
}
