package org.rag4j.rag.tracker;

import java.util.List;

public class RAGTracker {
    private static final ThreadLocal<RAGObserver> ragObserverThreadLocal = ThreadLocal.withInitial(RAGObserver::new);

    public static void setQuestion(String question) {
        ragObserverThreadLocal.get().setQuestion(question);
    }

    public static void setContext(String context) {
        ragObserverThreadLocal.get().setContext(context);
    }

    public static void setAnswer(String answer) {
        ragObserverThreadLocal.get().setAnswer(answer);
    }

    public static void addRelevantChunks(String documentChunkId, String text) {
        ragObserverThreadLocal.get().addRelevantChunk(documentChunkId, text);
    }

    public static void addWindowToChunkIds(String window, List<String> chunkIds) {
        ragObserverThreadLocal.get().addWindowToChunkIds(window, chunkIds);
    }

    public static void addWindowText(String window, String text) {
        ragObserverThreadLocal.get().addWindowText(window, text);
    }

    public static RAGObserver getRAGObserver() {
        return ragObserverThreadLocal.get();
    }

    public static void cleanup() {
        ragObserverThreadLocal.remove();
    }
}
