package org.rag4j.indexing;

import org.rag4j.rag.model.Chunk;

import java.util.ArrayList;
import java.util.List;

public class SplitterChain implements Splitter {
    private final List<Splitter> splitters;
    private final boolean includeAllChunks;

    public SplitterChain(List<Splitter> splitters) {
        this(splitters, false);
    }

    public SplitterChain(List<Splitter> splitters, boolean includeAllChunks) {
        this.splitters = splitters;
        this.includeAllChunks = includeAllChunks;

        if (splitters.isEmpty()) {
            throw new IllegalArgumentException("At least one splitter must be provided");
        }
    }

    @Override
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        return splitCurrentSplitter(inputDocument, 0, parentChunk);
    }

    private List<Chunk> splitCurrentSplitter(InputDocument inputDocument, int splitterNr, Chunk parentChunk) {
        List<Chunk> chunks = new ArrayList<>();
        List<Chunk> currentChunks = splitters.get(splitterNr).split(inputDocument, parentChunk);

        for (Chunk currentChunk : currentChunks) {
            // We always add the first and last chunk, or all chunks if include_all_chunks is True
            if (splitterNr + 1 >= splitters.size() || includeAllChunks || splitterNr == 0) {
                chunks.add(currentChunk);
            }

            // If we are not at the last splitter, we recursively call the next splitter
            if (splitterNr + 1 < splitters.size()) {
                List<Chunk> childChunks = splitCurrentSplitter(inputDocument, splitterNr + 1, currentChunk);
                chunks.addAll(childChunks);
            }
        }

        return chunks;
    }
}
