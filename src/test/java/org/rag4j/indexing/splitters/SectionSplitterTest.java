package org.rag4j.indexing.splitters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.model.Chunk;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SectionSplitterTest {
    private SectionSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new SectionSplitter();
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentHasMultipleSections() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2\n\nSection 3")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument);

        assertEquals(3, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("Section 2", chunks.get(1).getText());
        assertEquals("Section 3", chunks.get(2).getText());
    }

    @Test
    void shouldReturnEmptyListWhenInputDocumentIsEmpty() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument);

        assertTrue(chunks.isEmpty());
    }

    @Test
    void shouldReturnSingleChunkWhenInputDocumentHasSingleSection() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Single section")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, null);

        assertEquals(1, chunks.size());
        assertEquals("Single section", chunks.get(0).getText());
    }

    @Test
    void shouldCreateChunkIdForParentChunk() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2")
                .properties(Map.of())
                .build();
        Chunk parentChunk = Chunk.builder()
                .documentId("doc1")
                .chunkId("0")
                .totalChunks(1)
                .text("Section 1\n\nSection 2")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, parentChunk);

        assertEquals(2, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("Section 2", chunks.get(1).getText());
        assertEquals("0_0", chunks.get(0).getChunkId());
        assertEquals("0_1", chunks.get(1).getChunkId());
    }
}