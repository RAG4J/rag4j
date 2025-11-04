package org.rag4j.indexing.splitters;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.rag.model.Chunk;

import java.util.ArrayList;
import java.util.List;

public class SectionSplitter implements Splitter {

    @Override
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        String inputText = (parentChunk == null) ? inputDocument.getText() : parentChunk.getText();
        if (inputText.isBlank()) {
            return List.of();
        }
        String[] sections = inputText.split("\\n\\s*\\n");

        List<Chunk> chunks = new ArrayList<>();
        for (int i = 0; i < sections.length; i++) {
            String chunkId = (parentChunk == null) ? String.valueOf(i) : parentChunk.getChunkId() + "_" + i;
            Chunk chunk = Chunk.builder()
                    .documentId(inputDocument.getDocumentId())
                    .chunkId(chunkId)
                    .totalChunks(sections.length)
                    .text(sections[i])
                    .properties(inputDocument.getProperties())
                    .build();
            chunks.add(chunk);
        }

        return chunks;
    }

    public static String name() {
        return SectionSplitter.class.getSimpleName();
    }
}