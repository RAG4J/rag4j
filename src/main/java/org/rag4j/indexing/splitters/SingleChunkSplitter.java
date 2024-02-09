package org.rag4j.indexing.splitters;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.rag.model.Chunk;

import java.util.List;

/**
 * A splitter to transform the InputDocument into a single chunk.
 */
public class SingleChunkSplitter implements Splitter {
    @Override
    public List<Chunk> split(InputDocument inputDocument) {
        return List.of(Chunk.builder()
                .documentId(inputDocument.getDocumentId())
                .text(inputDocument.getText())
                .chunkId(0)
                .properties(inputDocument.getProperties())
                .build()
        );
    }
}
