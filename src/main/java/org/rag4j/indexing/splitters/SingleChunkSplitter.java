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
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        String textToSplit = parentChunk != null ? parentChunk.getText() : inputDocument.getText();
        String chunkId = parentChunk != null ? parentChunk.getChunkId() + "_0" : "0";
        return List.of(Chunk.builder()
                .documentId(inputDocument.getDocumentId())
                .text(textToSplit)
                .chunkId(chunkId)
                .properties(inputDocument.getProperties())
                .build()
        );
    }
}
