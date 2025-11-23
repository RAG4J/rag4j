package org.rag4j.indexing.splitters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.model.Chunk;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaxTokenSplitterTest {
    private MaxTokenSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new MaxTokenSplitter(5);
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentIsNotEmpty() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("hello java programmer, how are you doing?")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument);

        assertEquals(2, chunks.size());
        assertEquals("hello java programmer, how", chunks.get(0).getText());
        assertEquals(" are you doing?", chunks.get(1).getText());
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
    void shouldReturnSingleChunkWhenInputDocumentIsLessThanMaxTokens() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Hallo Java programmeur, hoe gaat het met je?")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument);

        assertEquals(3, chunks.size());
        assertEquals("Hallo Java programmeur,", chunks.get(0).getText());
        assertEquals(" hoe gaat het met je", chunks.get(1).getText());
        assertEquals("?", chunks.get(2).getText());
    }

    @Test
    void shouldCreateChunkIdForParentChunk() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("hello java programmer, how are you doing?")
                .properties(Map.of())
                .build();
        Chunk parentChunk = Chunk.builder()
                .documentId("doc1")
                .chunkId("0")
                .totalChunks(1)
                .text("hello java programmer, how are you doing?")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, parentChunk);

        assertEquals(2, chunks.size());
        assertEquals("hello java programmer, how", chunks.get(0).getText());
        assertEquals(" are you doing?", chunks.get(1).getText());
        assertEquals("0_0", chunks.get(0).getChunkId());
        assertEquals("0_1", chunks.get(1).getChunkId());
    }
}