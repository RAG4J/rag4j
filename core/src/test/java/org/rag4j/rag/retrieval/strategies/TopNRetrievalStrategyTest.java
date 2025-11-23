package org.rag4j.rag.retrieval.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.Retriever;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TopNRetrievalStrategyTest {
    @Mock
    Retriever retriever;

    TopNRetrievalStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TopNRetrievalStrategy(retriever);
    }

    @Test
    public void testRetrieveBasicFlow() {
        when(retriever.findRelevantChunks("just a question", 5)).thenReturn(List.of(
                new RelevantChunk("doc1", "1", 1, 0.9, "just an answer", Map.of()),
                new RelevantChunk("doc1", "2", 1,0.85,"just an answer", Map.of()),
                new RelevantChunk("doc3", "1", 1,0.7, "just an answer", Map.of()),
                new RelevantChunk("doc7", "1", 1,0.6, "just an answer", Map.of()),
                new RelevantChunk("doc2", "1", 1,0.55, "just an answer", Map.of())));

        RetrievalOutput retrievalOutput = strategy.retrieve("just a question", 5);

        assertEquals(retrievalOutput.getItems().size(), 5);
    }

    @Test
    public void testRetrieveBasicFlow_DefaultN() {
        when(retriever.findRelevantChunks("just a question", 4)).thenReturn(List.of(
                new RelevantChunk("doc1", "1", 1, 0.9, "just an answer", Map.of()),
                new RelevantChunk("doc1", "2", 1,0.85,"just an answer", Map.of()),
                new RelevantChunk("doc3", "1", 1,0.7, "just an answer", Map.of()),
                new RelevantChunk("doc2", "1", 1,0.55, "just an answer", Map.of())));

        RetrievalOutput retrievalOutput = strategy.retrieve("just a question", 4);

        assertEquals(retrievalOutput.getItems().size(), 4);
    }
}
