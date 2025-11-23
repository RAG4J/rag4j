package org.rag4j.indexing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.indexing.splitters.SingleChunkSplitter;
import org.rag4j.indexing.splitters.SectionSplitter;
import org.rag4j.rag.model.Chunk;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SplitterChainTest {
    private SplitterChain splitterChainIncludeAll;
    private SplitterChain splitterChainExcludeAll;

    @BeforeEach
    void setUp() {
        List<Splitter> splitters = List.of(new SectionSplitter(), new SingleChunkSplitter());
        splitterChainIncludeAll = new SplitterChain(splitters, true);
        splitterChainExcludeAll = new SplitterChain(splitters, false);
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentHasMultipleSplittersIncludeAllChunksTrue() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2\n\nSection 3")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitterChainIncludeAll.split(inputDocument, null);

        assertEquals(6, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
        assertEquals("Section 1", chunks.get(1).getText());
        assertEquals("0_0", chunks.get(1).getChunkId());
        assertEquals("Section 2", chunks.get(2).getText());
        assertEquals("1", chunks.get(2).getChunkId());
        assertEquals("Section 2", chunks.get(3).getText());
        assertEquals("1_0", chunks.get(3).getChunkId());
        assertEquals("Section 3", chunks.get(4).getText());
        assertEquals("2", chunks.get(4).getChunkId());
        assertEquals("Section 3", chunks.get(5).getText());
        assertEquals("2_0", chunks.get(5).getChunkId());
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentHasMultipleSplittersIncludeAllChunksFalse() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2\n\nSection 3")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitterChainExcludeAll.split(inputDocument, null);

        assertEquals(6, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
        assertEquals("Section 1", chunks.get(1).getText());
        assertEquals("0_0", chunks.get(1).getChunkId());
        assertEquals("Section 2", chunks.get(2).getText());
        assertEquals("1", chunks.get(2).getChunkId());
        assertEquals("Section 2", chunks.get(3).getText());
        assertEquals("1_0", chunks.get(3).getChunkId());
        assertEquals("Section 3", chunks.get(4).getText());
        assertEquals("2", chunks.get(4).getChunkId());
        assertEquals("Section 3", chunks.get(5).getText());
        assertEquals("2_0", chunks.get(5).getChunkId());
    }

    @Test
    void shouldReturnEmptyListWhenInputDocumentIsEmpty() {
        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitterChainIncludeAll.split(inputDocument, null);

        assertTrue(chunks.isEmpty());
    }

    @Test
    void shouldReturnSingleChunkWhenInputDocumentHasSingleSplitter() {
        List<Splitter> singleSplitter = List.of(new SingleChunkSplitter());
        SplitterChain singleSplitterChain = new SplitterChain(singleSplitter, true);

        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Single section")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = singleSplitterChain.split(inputDocument, null);

        assertEquals(1, chunks.size());
        assertEquals("Single section", chunks.get(0).getText());
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentHasThreeSplitters() {
        List<Splitter> splitters = List.of(new SectionSplitter(), new SingleChunkSplitter(), new SectionSplitter());
        SplitterChain splitterChain = new SplitterChain(splitters, true);

        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2\n\nSection 3")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitterChain.split(inputDocument, null);

        assertEquals(9, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
        assertEquals("Section 1", chunks.get(1).getText());
        assertEquals("0_0", chunks.get(1).getChunkId());
        assertEquals("Section 1", chunks.get(2).getText());
        assertEquals("0_0_0", chunks.get(2).getChunkId());
        assertEquals("Section 2", chunks.get(3).getText());
        assertEquals("1", chunks.get(3).getChunkId());
        assertEquals("Section 2", chunks.get(4).getText());
        assertEquals("1_0", chunks.get(4).getChunkId());
        assertEquals("Section 2", chunks.get(5).getText());
        assertEquals("1_0_0", chunks.get(5).getChunkId());
        assertEquals("Section 3", chunks.get(6).getText());
        assertEquals("2", chunks.get(6).getChunkId());
        assertEquals("Section 3", chunks.get(7).getText());
        assertEquals("2_0", chunks.get(7).getChunkId());
        assertEquals("Section 3", chunks.get(8).getText());
        assertEquals("2_0_0", chunks.get(8).getChunkId());
    }

    @Test
    void shouldSplitIntoChunksWhenInputDocumentHasThreeSplittersNotExcludeAll() {
        List<Splitter> splitters = List.of(new SectionSplitter(), new SingleChunkSplitter(), new SectionSplitter());
        SplitterChain splitterChain = new SplitterChain(splitters, false);

        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text("Section 1\n\nSection 2\n\nSection 3")
                .properties(Map.of())
                .build();

        List<Chunk> chunks = splitterChain.split(inputDocument, null);

        assertEquals(6, chunks.size());
        assertEquals("Section 1", chunks.get(0).getText());
        assertEquals("0", chunks.get(0).getChunkId());
        assertEquals("Section 1", chunks.get(1).getText());
        assertEquals("0_0_0", chunks.get(1).getChunkId());
        assertEquals("Section 2", chunks.get(2).getText());
        assertEquals("1", chunks.get(2).getChunkId());
        assertEquals("Section 2", chunks.get(3).getText());
        assertEquals("1_0_0", chunks.get(3).getChunkId());
        assertEquals("Section 3", chunks.get(4).getText());
        assertEquals("2", chunks.get(4).getChunkId());
        assertEquals("Section 3", chunks.get(5).getText());
        assertEquals("2_0_0", chunks.get(5).getChunkId());
    }
}