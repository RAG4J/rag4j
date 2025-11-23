package org.rag4j.local.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InternalContentStoreTest {
    private InternalContentStore store;

    @BeforeEach
    void setUp() {
        Embedder embedder = new MockEmbedder();
        this.store = new InternalContentStore(embedder);
        List<Chunk> chunks = Arrays.asList(
                Chunk.builder().chunkId("1").documentId("doc1").totalChunks(1).text("Sample text").properties(Map.of("author", "Jettro", "ingestion", new Date())).build(),
                Chunk.builder().chunkId("2").documentId("doc2").totalChunks(1).text("Sample text two").properties(Map.of("author", "Jettro", "ingestion", new Date())).build()
        );
        store.store(chunks);
    }

    @Test
    void findRelevantChunks() {
        // Arrange

        // Act
        List<RelevantChunk> relevantChunks = store.findRelevantChunks("test", 2);

        // Assert
        assertNotNull(relevantChunks);
        assertEquals(2, relevantChunks.size());
        assertTrue(relevantChunks.get(0).getScore() <= relevantChunks.get(1).getScore());
    }

    @Test
    void getChunk() {
        // Arrange

        // Act
        Chunk chunk = store.getChunk("doc1", "1");

        // Assert
        assertNotNull(chunk);
        assertEquals("doc1", chunk.getDocumentId());
        assertEquals("1", chunk.getChunkId());
        assertEquals(1, chunk.getTotalChunks());
        assertEquals("Sample text", chunk.getText());
        assertEquals("Jettro", chunk.getProperties().get("author"));
    }

    @Test
    void loopOverChunks() {
        final int[] callCount = {0};
        this.store.loopOverChunks(chunk -> {
            assertNotNull(chunk);
            assertNotNull(chunk.getDocumentId());
            assertNotNull(chunk.getChunkId());
            assertNotNull(chunk.getText());
            assertNotNull(chunk.getProperties());
            callCount[0]++;
        });
        assertEquals(2, callCount[0]);
    }

    @Test
    void backupToDisk() {

    }

    private static class MockEmbedder implements Embedder {
        @Override
        public String identifier() {
            return "MockEmbedder";
        }

        @Override
        public String supplier() {
            return "MockSupplier";
        }

        @Override
        public String model() {
            return "MockModel";
        }

        @Override
        public List<Float> embed(String text) {
            return Arrays.asList(1.0f, 2.0f, 3.0f);
        }
    }
}