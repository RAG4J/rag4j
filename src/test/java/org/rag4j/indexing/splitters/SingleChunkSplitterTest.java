package org.rag4j.indexing.splitters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.model.Chunk;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleChunkSplitterTest {
    private SingleChunkSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new SingleChunkSplitter();
    }

    @Test
    void shouldSplitIntoSingleChunkWhenInputDocumentIsNotEmpty() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("This is a test document.")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, null);

        assertEquals(1, chunks.size());
        assertEquals("This is a test document.", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
    }

    @Test
    void shouldReturnSingleEmptyChunkWhenInputDocumentIsEmpty() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, null);

        assertEquals(1, chunks.size());
        assertEquals("", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
    }

    @Test
    void shouldCreateChunkIdForParentChunk() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("This is a test document.")
                .properties(Map.of())
                .build();
        Chunk parentChunk = Chunk.builder()
                .documentId("doc1")
                .chunkId("0")
                .totalChunks(1)
                .text("This is a test document.")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitter.split(inputDocument, parentChunk);

        assertEquals(1, chunks.size());
        assertEquals("This is a test document.", chunks.get(0).getText());
        assertEquals("0_0", chunks.get(0).getChunkId());
    }
}