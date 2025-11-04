package org.rag4j.rag.retrieval.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.Retriever;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HierarchicalRetrievalStrategyTest {

    private Retriever retriever;
    private HierarchicalRetrievalStrategy strategy;

    @BeforeEach
    void setUp() {
        retriever = mock(Retriever.class);
        RelevantChunk relevantChunk = new RelevantChunk("doc1", "1_3_2", 4, 0.7d, "text1", Map.of());
        when(retriever.findRelevantChunks(anyString(), anyInt())).thenReturn(List.of(relevantChunk));
        when(retriever.getChunk(anyString(), anyString())).thenAnswer(invocation -> {
            String chunkId = invocation.getArgument(1);
            return mockGetChunk(invocation.getArgument(0), chunkId);
        });
        strategy = new HierarchicalRetrievalStrategy(retriever, 1);
    }

    private Chunk mockGetChunk(String documentId, String chunkId) {
        Map<String, Chunk> chunks = Map.of(
                "1_3_2", newChunk(documentId, chunkId, 4, "hierarchical_text layer 3", Map.of()),
                "1_3_3", newChunk(documentId, chunkId, 4, "hierarchical_text layer 3 item 2", Map.of()),
                "1_3", newChunk(documentId, chunkId, 2, "hierarchical_text layer 2", Map.of()),
                "1", newChunk(documentId, chunkId, 2, "hierarchical_text layer 1", Map.of())
        );
        return chunks.get(chunkId);
    }

    @Test
    void testRetrieveMaxResultsMaxLevels1() {
        strategy = new HierarchicalRetrievalStrategy(retriever, 1);
        RetrievalOutput output = strategy.retrieve("question", 1);

        assertNotNull(output);
        assertEquals(1, output.getItems().size());
        assertEquals("doc1", output.getItems().getFirst().getDocumentId());
        assertEquals("1_3_2", output.getItems().getFirst().getChunkId());
        assertEquals("hierarchical_text layer 2", output.getItems().getFirst().getText());
    }

    @Test
    void testRetrieveMaxResultsMaxLevels2() {
        strategy = new HierarchicalRetrievalStrategy(retriever, 2);
        RetrievalOutput output = strategy.retrieve("question", 1);

        assertNotNull(output);
        assertEquals(1, output.getItems().size());
        assertEquals("1_3_2", output.getItems().getFirst().getChunkId());
        assertEquals("hierarchical_text layer 1", output.getItems().getFirst().getText());
    }

    @Test
    void testRetrieveMaxResultsMaxLevelsTooHigh() {
        strategy = new HierarchicalRetrievalStrategy(retriever, 10);
        RetrievalOutput output = strategy.retrieve("question", 1);

        assertNotNull(output);
        assertEquals(1, output.getItems().size());
        assertEquals("1_3_2", output.getItems().getFirst().getChunkId());
        assertEquals("hierarchical_text layer 1", output.getItems().getFirst().getText());
    }

    @Test
    void testRetrieveMaxResultsMaxLevels1Deduplicate() {
        Retriever dedupRetriever = mock(Retriever.class);
        when(dedupRetriever.findRelevantChunks(anyString(), anyInt())).thenReturn(
                List.of(
                        new RelevantChunk("doc1", "1_3_2", 4,0.7d, "text1", Map.of()),
                        new RelevantChunk("doc1", "1_3_3", 4, 0.6d,"text2", Map.of())
                )
        );
        when(dedupRetriever.getChunk(anyString(), anyString())).thenAnswer(invocation -> {
            String chunkId = invocation.getArgument(1);
            return mockGetChunk(invocation.getArgument(0), chunkId);
        });
        HierarchicalRetrievalStrategy dedupStrategy = new HierarchicalRetrievalStrategy(dedupRetriever, 1);

        RetrievalOutput output = dedupStrategy.retrieve("question", 1);

        assertNotNull(output);
        assertEquals(1, output.getItems().size());
        assertEquals("doc1", output.getItems().getFirst().getDocumentId());
        assertEquals("1_3_2", output.getItems().getFirst().getChunkId());
        assertEquals("hierarchical_text layer 2", output.getItems().getFirst().getText());
    }

    private Chunk newChunk(String docId, String chunkId, int totalChunks, String text, Map<String, Object> properties) {
        return Chunk.builder()
                .documentId(docId)
                .chunkId(chunkId)
                .totalChunks(totalChunks)
                .text(text)
                .properties(properties)
                .build();
    }

}