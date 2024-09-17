package org.rag4j.rag.retrieval;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.tracker.RAGObserver;
import org.rag4j.rag.tracker.RAGTracker;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservedRetrieverTest {
    @Mock
    Retriever retriever;

    @Test
    public void testFindRelevantChunks() {
        when(retriever.findRelevantChunks("just a question", 3)).thenReturn(List.of(
                new RelevantChunk("doc1", "1", 11, 0.9, "just an answer", Map.of()),
                new RelevantChunk("doc1", "6", 11,0.85,"just an answer", Map.of()),
                new RelevantChunk("doc2", "11", 17,0.55, "just an answer", Map.of())));


        ObservedRetriever observedRetriever = new ObservedRetriever(retriever);

        observedRetriever.findRelevantChunks("just a question", 3);

        verify(retriever, times(1)).findRelevantChunks("just a question", 3);

        RAGObserver observer = RAGTracker.getRAGObserver();
        assertEquals(observer.getRelevantChunks().size(), 3);
    }

    @Test
    public void testFindRelevantChunksNothingFound() {
        when(retriever.findRelevantChunks("just a question", 3)).thenReturn(List.of());

        ObservedRetriever observedRetriever = new ObservedRetriever(retriever);

        observedRetriever.findRelevantChunks("just a question", 3);

        verify(retriever, times(1)).findRelevantChunks("just a question", 3);

        RAGObserver observer = RAGTracker.getRAGObserver();
        assertEquals(observer.getRelevantChunks().size(), 0);
    }

}