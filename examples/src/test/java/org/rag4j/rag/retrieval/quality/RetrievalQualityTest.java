package org.rag4j.rag.retrieval.quality;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.Retriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RetrievalQualityTest {

    @Mock
    private Retriever retriever;

    @Mock
    private Embedder embedder;

    private RetrievalQualityService retrievalQualityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        retrievalQualityService = new RetrievalQualityService(retriever);
    }

    @Test
    public void obtainRetrievalQualityReturnsCorrectQuality() {
        List<QuestionAnswerRecord> questionAnswerRecords = new ArrayList<>();
        questionAnswerRecords.add(new QuestionAnswerRecord("doc1", "1", "text", "question"));

        RelevantChunk relevantChunk = new RelevantChunk(Chunk.builder()
                .documentId("doc1")
                .chunkId("1")
                .totalChunks(3)
                .text("text")
                .properties(Map.of())
                .build(), 1.0);
        when(retriever.findRelevantChunks(anyString(), any(List.class), anyInt())).thenReturn(List.of(relevantChunk));

        RetrievalQuality retrievalQuality = retrievalQualityService.obtainRetrievalQuality(questionAnswerRecords, embedder);

        assertEquals(1, retrievalQuality.getCorrect().size());
        assertEquals(0, retrievalQuality.getIncorrect().size());
    }

    @Test
    public void obtainRetrievalQualityReturnsIncorrectQuality() {
        List<QuestionAnswerRecord> questionAnswerRecords = new ArrayList<>();
        questionAnswerRecords.add(new QuestionAnswerRecord("doc1", "1", "text", "question"));

        RelevantChunk relevantChunk = new RelevantChunk("doc2", "2", 3, 1.0,"text", Map.of());
        when(retriever.findRelevantChunks(anyString(), any(List.class), anyInt())).thenReturn(List.of(relevantChunk));

        RetrievalQuality retrievalQuality = retrievalQualityService.obtainRetrievalQuality(questionAnswerRecords, embedder);

        assertEquals(0, retrievalQuality.getCorrect().size());
        assertEquals(1, retrievalQuality.getIncorrect().size());
    }
}