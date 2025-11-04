package org.rag4j.indexing.splitters;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.rag.model.Chunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits an {@link InputDocument} into {@link Chunk}s of a maximum number of tokens. The tokens are obtained by
 * encoding the text of the document using the {@link EncodingType#CL100K_BASE} encoding. The chunks of tokens are
 * decoded back into text.
 */
public class MaxTokenSplitter implements Splitter {
    private final Encoding encoding;
    private final int maxTokens;

    public MaxTokenSplitter(int maxTokens) {
        this.maxTokens = maxTokens;
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        this.encoding = registry.getEncoding(EncodingType.CL100K_BASE);
    }

    @Override
    public List<Chunk> split(InputDocument inputDocument, Chunk parentChunk) {
        String textToSplit = parentChunk != null ? parentChunk.getText() : inputDocument.getText();

        List<Integer> tokens = this.encoding.encode(textToSplit).boxed();
        List<Chunk> chunks = new ArrayList<>();

        int numChunks = (tokens.size() / maxTokens) + (tokens.size() % maxTokens == 0 ? 0 : 1);

        while (!tokens.isEmpty()) {
            int chunkSize = Math.min(tokens.size(), this.maxTokens);
            List<Integer> chunkTokens = tokens.subList(0, chunkSize);
            tokens = tokens.subList(chunkSize, tokens.size());
            String chunkSizeStr = String.valueOf(chunks.size());
            String chunkId = parentChunk != null ? parentChunk.getChunkId() + "_" + chunkSizeStr : chunkSizeStr;

            String chunkText = this.encoding.decode(toIntArray(chunkTokens));
            Chunk chunk = Chunk.builder()
                    .documentId(inputDocument.getDocumentId())
                    .chunkId(chunkId)
                    .totalChunks(numChunks)
                    .text(chunkText)
                    .properties(inputDocument.getProperties())
                    .build();
            chunks.add(chunk);
        }
        return chunks;
    }

    private IntArrayList toIntArray(List<Integer> chunkTokens) {
        IntArrayList intArrayList = new IntArrayList(chunkTokens.size());
        for (Integer token : chunkTokens) {
            intArrayList.add(token);
        }
        return intArrayList;
    }
}
