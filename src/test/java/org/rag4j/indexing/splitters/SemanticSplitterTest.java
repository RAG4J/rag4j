package org.rag4j.indexing.splitters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.generation.knowledge.Knowledge;
import org.rag4j.rag.generation.knowledge.KnowledgeExtractorService;
import org.rag4j.rag.model.Chunk;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SemanticSplitterTest {

    private KnowledgeExtractorService knowledgeExtractorService;
    private SemanticSplitter semanticSplitter;

    @BeforeEach
    void setUp() {
        knowledgeExtractorService = mock(KnowledgeExtractorService.class);
        semanticSplitter = new SemanticSplitter(knowledgeExtractorService);
    }

    @Test
    void shouldSplitIntoChunksBasedOnKnowledgeExtraction() {
        String text = "Sample text for knowledge extraction.";
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text(text)
                .properties(Map.of())
                .build();

        List<Knowledge> knowledgeList = List.of(
                Knowledge.builder().subject("Subject 1").description("Description 1").build(),
                Knowledge.builder().subject("Subject 2").description("Description 2").build()
        );

        when(knowledgeExtractorService.extractKnowledge(text)).thenReturn(knowledgeList);

        List<Chunk> chunks = semanticSplitter.split(inputDocument, null);

        assertEquals(2, chunks.size());
        assertEquals("Subject 1: Description 1", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
        assertEquals("Subject 2: Description 2", chunks.get(1).getText());
        assertEquals("1", chunks.get(1).getChunkId());
    }
}