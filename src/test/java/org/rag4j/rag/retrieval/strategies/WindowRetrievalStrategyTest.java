package org.rag4j.rag.retrieval.strategies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.rag.tracker.RAGObserver;
import org.rag4j.rag.tracker.RAGTracker;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WindowRetrievalStrategyTest {
    @Mock
    Retriever retriever;

    @Test
    public void testRetrieveBasicFlow() {
        WindowRetrievalStrategy strategy = new WindowRetrievalStrategy(retriever, 2);

        when(retriever.findRelevantChunks("just a question", 3)).thenReturn(List.of(
                new RelevantChunk("doc1", "1", 11, 0.9, "just an answer", Map.of()),
                new RelevantChunk("doc1", "6", 11, 0.85, "just an answer", Map.of()),
                new RelevantChunk("doc2", "11", 17, 0.55, "just an answer", Map.of())));

        when(retriever.getChunk(anyString(), anyString())).thenReturn(
                newChunk("doc1", 0, 11, "chunk 0", Map.of()),
                newChunk("doc1", 1, 11, "just an answer", Map.of()),
                newChunk("doc1", 2, 11, "chunk 2", Map.of()),
                newChunk("doc1", 3, 11, "chunk 3", Map.of()),
                newChunk("doc1", 4, 11, "chunk 4", Map.of()),
                newChunk("doc1", 5, 11, "chunk 5", Map.of()),
                newChunk("doc1", 6, 11, "just an answer", Map.of()),
                newChunk("doc1", 7, 11, "chunk 7", Map.of()),
                newChunk("doc1", 8, 11, "chunk 8", Map.of()),
                newChunk("doc2", 9, 17, "chunk 9", Map.of()),
                newChunk("doc2", 10, 17, "chunk 10", Map.of()),
                newChunk("doc2", 11, 17, "just an answer", Map.of()),
                newChunk("doc2", 12, 17, "chunk 12", Map.of()),
                newChunk("doc2", 13, 17, "chunk 13", Map.of())

        );

        RetrievalOutput retrievalOutput = strategy.retrieve("just a question", 3);

        assertEquals(3, retrievalOutput.getItems().size());
        assertEquals("doc1", retrievalOutput.getItems().getFirst().getDocumentId());
        assertEquals("chunk 0 just an answer chunk 2 chunk 3", retrievalOutput.getItems().getFirst().getText());
        assertEquals("doc1", retrievalOutput.getItems().get(1).getDocumentId());
        assertEquals("chunk 4 chunk 5 just an answer chunk 7 chunk 8", retrievalOutput.getItems().get(1).getText());
        assertEquals("doc2", retrievalOutput.getItems().get(2).getDocumentId());
        assertEquals("chunk 9 chunk 10 just an answer chunk 12 chunk 13", retrievalOutput.getItems().get(2).getText());

        // Verify that all when actions are called exactly once
        verify(retriever, times(1)).findRelevantChunks("just a question", 3);
        verify(retriever, times(1)).getChunk("doc1", "0");
        verify(retriever, times(1)).getChunk("doc1", "1");
        verify(retriever, times(1)).getChunk("doc1", "2");
        verify(retriever, times(1)).getChunk("doc1", "3");
        verify(retriever, times(1)).getChunk("doc1", "4");
        verify(retriever, times(1)).getChunk("doc1", "5");
        verify(retriever, times(1)).getChunk("doc1", "6");
        verify(retriever, times(1)).getChunk("doc1", "7");
        verify(retriever, times(1)).getChunk("doc1", "8");
        verify(retriever, times(1)).getChunk("doc2", "9");
        verify(retriever, times(1)).getChunk("doc2", "10");
        verify(retriever, times(1)).getChunk("doc2", "11");
        verify(retriever, times(1)).getChunk("doc2", "12");
        verify(retriever, times(1)).getChunk("doc2", "13");

    }

    private Chunk newChunk(String docId, int chunkId, int totalChunks, String text, Map<String, Object> properties) {
        return Chunk.builder()
                .documentId(docId)
                .chunkId(String.valueOf(chunkId))
                .totalChunks(totalChunks)
                .text(text)
                .properties(properties)
                .build();
    }

    @Test
    public void testRetrieveBasicFlowObserver() {
        WindowRetrievalStrategy strategy = new WindowRetrievalStrategy(retriever, 1);

        when(retriever.findRelevantChunks("just a question", 2)).thenReturn(List.of(
                new RelevantChunk("doc1", "1", 11, 0.9, "just an answer", Map.of()),
                new RelevantChunk("doc2", "6", 9, 0.85, "another one", Map.of()))
        );

        when(retriever.getChunk(anyString(), anyString())).thenReturn(
                newChunk("doc1", 0, 11, "chunk 0", Map.of()),
                newChunk("doc1", 1, 11, "just an answer", Map.of()),
                newChunk("doc1", 2, 11, "chunk 2", Map.of()),
                newChunk("doc2", 5, 9, "chunk 5", Map.of()),
                newChunk("doc2", 6, 9, "another one", Map.of()),
                newChunk("doc2", 7, 9, "chunk 7", Map.of())
        );

        RetrievalOutput retrievalOutput = strategy.retrieve("just a question", 2, true);

        assertEquals(2, retrievalOutput.getItems().size());
        assertEquals("doc1", retrievalOutput.getItems().getFirst().getDocumentId());
        assertEquals("chunk 0 just an answer chunk 2", retrievalOutput.getItems().getFirst().getText());
        assertEquals("doc2", retrievalOutput.getItems().get(1).getDocumentId());
        assertEquals("chunk 5 another one chunk 7", retrievalOutput.getItems().get(1).getText());

        // Verify that all when actions are called exactly once
        verify(retriever, times(1)).findRelevantChunks("just a question", 2);
        verify(retriever, times(1)).getChunk("doc1", "0");
        verify(retriever, times(1)).getChunk("doc1", "1");
        verify(retriever, times(1)).getChunk("doc1", "2");
        verify(retriever, times(1)).getChunk("doc2", "5");
        verify(retriever, times(1)).getChunk("doc2", "6");
        verify(retriever, times(1)).getChunk("doc2", "7");


        RAGObserver observer = RAGTracker.getRAGObserver();
        // Now check the observer
        assertEquals(2, observer.getWindowTexts().size());
        assertEquals(2, observer.getWindowToChunkIds().size());

        assertEquals(3, observer.getWindowToChunkIds().get("doc1_1").size());
        assertEquals("0", observer.getWindowToChunkIds().get("doc1_1").get(0));
        assertEquals("1", observer.getWindowToChunkIds().get("doc1_1").get(1));
        assertEquals("2", observer.getWindowToChunkIds().get("doc1_1").get(2));

        assertEquals(3, observer.getWindowToChunkIds().get("doc2_6").size());
        assertEquals("5", observer.getWindowToChunkIds().get("doc2_6").get(0));
        assertEquals("6", observer.getWindowToChunkIds().get("doc2_6").get(1));
        assertEquals("7", observer.getWindowToChunkIds().get("doc2_6").get(2));

        assertEquals("chunk 0 just an answer chunk 2", observer.getWindowTexts().get("doc1_1"));
        assertEquals("chunk 5 another one chunk 7", observer.getWindowTexts().get("doc2_6"));
    }


    @Test
    public void testGetChunkIds() {
        // TODO FIX THIS TEST, cannot compare Lists this way
        // A Normal scenario
        assertEquals(
                List.of("3", "4", "5", "6", "7"),
                WindowRetrievalStrategy.getChunkIds("5", 2, 10)
        );

        // A scenario where the window cannot be taken from the beginning
        assertEquals(
                List.of("0", "1", "2", "3"),
                WindowRetrievalStrategy.getChunkIds("1", 2, 10)
        );

        // A scenario where the window cannot be taken from the end
        assertEquals(
                List.of("7", "8", "9"),
                WindowRetrievalStrategy.getChunkIds("9", 2, 10)
        );

        // A scenario where the window is 0
        assertEquals(
                List.of("5"),
                WindowRetrievalStrategy.getChunkIds("5", 0, 7)
        );

        assertEquals(
                List.of("0", "1"),
                WindowRetrievalStrategy.getChunkIds("1", 1, 2)
        );
    }
}
