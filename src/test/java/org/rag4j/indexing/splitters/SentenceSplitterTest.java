package org.rag4j.indexing.splitters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.model.Chunk;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SentenceSplitterTest {

    @Mock
    private InputDocument mockInputDocument;

    private SentenceSplitter sentenceSplitter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sentenceSplitter = new SentenceSplitter();
    }

    @Test
    @DisplayName("splits input document into chunks")
    void splitsInputDocumentIntoChunks() {
        when(mockInputDocument.getText()).thenReturn("This is sentence one. This is sentence two.");

        List<Chunk> chunks = sentenceSplitter.split(mockInputDocument);

        assertEquals(2, chunks.size());
        assertEquals("This is sentence one.", chunks.get(0).getText());
        assertEquals("This is sentence two.", chunks.get(1).getText());
    }

    @Test
    @DisplayName("splits input document into chunks")
    void splitsInputDocumentIntoChunksWithMoreInput() {
        when(mockInputDocument.getText()).thenReturn("This is the title.     " +
                "This is sentence one. This is sentence two with a number like 5.6 in it.");

        List<Chunk> chunks = sentenceSplitter.split(mockInputDocument);

        assertEquals(3, chunks.size());
        assertEquals("This is the title.", chunks.get(0).getText());
        assertEquals("This is sentence one.", chunks.get(1).getText());
        assertEquals("This is sentence two with a number like 5.6 in it.", chunks.get(2).getText());
    }


    @Test
    @DisplayName("returns empty list when input document is empty")
    void returnsEmptyListWhenInputDocumentIsEmpty() {
        when(mockInputDocument.getText()).thenReturn("");

        List<Chunk> chunks = sentenceSplitter.split(mockInputDocument);

        assertTrue(chunks.isEmpty());
    }

    @Test
    @DisplayName("throws exception when input document is null")
    void throwsExceptionWhenInputDocumentIsNull() {
        assertThrows(NullPointerException.class, () -> sentenceSplitter.split(null));
    }
}