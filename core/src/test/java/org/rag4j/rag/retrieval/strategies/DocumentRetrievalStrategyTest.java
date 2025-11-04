package org.rag4j.rag.retrieval.strategies;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.Retriever;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class DocumentRetrievalStrategyTest {

    @Test
    public void retrieveShouldReturnCorrectOutputForGivenQuestion() {
        Retriever retriever = Mockito.mock(Retriever.class);
        DocumentRetrievalStrategy strategy = new DocumentRetrievalStrategy(retriever);
        RelevantChunk relevantChunk = new RelevantChunk("doc1", "0", 1, 1.0, "text text", Map.of("key1","value1"));
        Chunk chunk = Chunk.builder().
                documentId("doc1").
                chunkId("0").
                totalChunks(1).
                text("text text").
                properties(Map.of("key1","value1")).
                build();
        when(retriever.findRelevantChunks("question", 1)).thenReturn(Collections.singletonList(relevantChunk));
        when(retriever.getChunk("doc1", "0")).thenReturn(chunk);
        when(retriever.getChunk("doc1", "1")).thenReturn(chunk);

        RetrievalOutput expected = RetrievalOutput.builder()
                .items(Collections.singletonList(
                        RetrievalOutput.RetrievalOutputItem.builder()
                                .documentId("doc1")
                                .chunkId("0")
                                .text("text text\nkey1: value1")
                                .build()))
                .build();

        RetrievalOutput retrieved = strategy.retrieve("question", 1);
        assertEquals(expected, retrieved);
    }

    @Test
    public void retrieveShouldReturnEmptyOutputForNoRelevantChunks() {
        Retriever retriever = Mockito.mock(Retriever.class);
        DocumentRetrievalStrategy strategy = new DocumentRetrievalStrategy(retriever);
        when(retriever.findRelevantChunks("question", 1)).thenReturn(Collections.emptyList());

        RetrievalOutput expected = RetrievalOutput.builder().build();

        assertEquals(expected, strategy.retrieve("question", 1));
    }

    @Test
    public void retrieveShouldReturnCorrectOutputForGivenQuestionAndVector() {
        Retriever retriever = Mockito.mock(Retriever.class);
        DocumentRetrievalStrategy strategy = new DocumentRetrievalStrategy(retriever);
        RelevantChunk relevantChunk = new RelevantChunk("doc1", "0", 1, 1.0, "text text", Map.of());
        Chunk chunk = Chunk.builder().
                documentId("doc1").
                chunkId("0").
                totalChunks(1).
                text("text text").
                properties(Map.of()).
                build();
        List<Float> vector = Arrays.asList(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        when(retriever.findRelevantChunks("question", vector, 1)).thenReturn(Collections.singletonList(relevantChunk));
        when(retriever.getChunk("doc1", "0")).thenReturn(chunk);
        when(retriever.getChunk("doc1", "1")).thenReturn(chunk);

        RetrievalOutput expected = RetrievalOutput.builder()
                .items(Collections.singletonList(
                        RetrievalOutput.RetrievalOutputItem.builder()
                                .documentId("doc1")
                                .chunkId("0")
                                .text("text text")
                                .build()))
                .build();

        RetrievalOutput question = strategy.retrieve("question", vector, 1);
        assertTrue(expected.equals(question));
    }

    @Test
    public void retrieveShouldReturnEmptyOutputForNoRelevantChunksAndGivenVector() {
        Retriever retriever = Mockito.mock(Retriever.class);
        DocumentRetrievalStrategy strategy = new DocumentRetrievalStrategy(retriever);
        List<Float> vector = Arrays.asList(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        when(retriever.findRelevantChunks("question", vector, 1)).thenReturn(Collections.emptyList());

        RetrievalOutput expected = RetrievalOutput.builder().build();

        assertEquals(expected, strategy.retrieve("question", vector, 1));
    }
}