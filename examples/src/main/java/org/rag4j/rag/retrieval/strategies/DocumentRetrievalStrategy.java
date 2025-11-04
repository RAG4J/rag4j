package org.rag4j.rag.retrieval.strategies;


import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.RetrievalStrategy;
import org.rag4j.rag.retrieval.Retriever;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This strategy is used to retrieve a document from the retriever. It does this by combining all the chunks of a
 * document into one text. You can also create an embedding for the complete document, but having smaller chunks
 * for the matching often works better.
 */
public class DocumentRetrievalStrategy implements RetrievalStrategy {
    private final Retriever retriever;

    public DocumentRetrievalStrategy(Retriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public RetrievalOutput retrieve(String question, int maxResults) {
        List<RelevantChunk> relevantItems = retriever.findRelevantChunks(question, maxResults);

        return extractDocumentFromRelevantChunk(relevantItems);
    }

    @Override
    public RetrievalOutput retrieve(String question, List<Float> vector, int maxResults) {
        List<RelevantChunk> relevantItems = retriever.findRelevantChunks(question, vector, maxResults);

        return extractDocumentFromRelevantChunk(relevantItems);
    }

    private RetrievalOutput extractDocumentFromRelevantChunk(List<RelevantChunk> relevantItems) {
        // Remove chunks from the same document
        List<RelevantChunk> relevantItemsWithoutDuplicates = relevantItems.stream()
                .collect(Collectors.groupingBy(RelevantChunk::getDocumentId))
                .values()
                .stream()
                .map(List::getFirst)
                .toList();

        List<RetrievalOutput.RetrievalOutputItem> outputItems = relevantItemsWithoutDuplicates
                .stream()
                .map(this::getDocumentChunksByRelevantChunk)
                .toList();
        return outputItems.isEmpty() ?
                RetrievalOutput.builder().build() : RetrievalOutput.builder().items(outputItems).build();
    }

    private RetrievalOutput.RetrievalOutputItem getDocumentChunksByRelevantChunk(RelevantChunk relevantChunk) {
        List<String> chunkIds = createChunkArrayForDocument(relevantChunk.getTotalChunks());
        StringBuilder text = new StringBuilder(combineChunksTexts(relevantChunk, chunkIds));

        for (Map.Entry<String, Object> entry : relevantChunk.getProperties().entrySet()) {
            text.append("\n").append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
        }

        return RetrievalOutput.RetrievalOutputItem.builder()
                .documentId(relevantChunk.getDocumentId())
                .chunkId(relevantChunk.getChunkId())
                .text(text.toString().trim())
                .build();
    }

    List<String> createChunkArrayForDocument(int n) {
        List<String> chunkIds = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            chunkIds.add(String.valueOf(i));
        }
        return chunkIds;
    }

    private String combineChunksTexts(RelevantChunk relevantChunk, List<String> chunkIds) {
        return chunkIds.stream()
                .map(chunkId -> retriever.getChunk(relevantChunk.getDocumentId(), chunkId))
                .map(Chunk::getText)
                .collect(Collectors.joining(" "));
    }
}

