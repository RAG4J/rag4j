package org.rag4j.rag.retrieval.strategies;

import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.RetrievalStrategy;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.rag.tracker.RAGObserver;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.tracker.RAGTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>A retrieval strategy that returns a window of chunks around the relevant chunks. The window size is configurable.
 * This strategy is useful when the relevant chunks are not enough to provide context. The strategy does require
 * something from the chunks that are obtained by the retriever. The chunks need to have a unique id that is a sequence
 * of integers. The first chunk of a document has chunkId 1, the second chunkId 2, etc. The total number of chunks in a
 * document is also required. This is needed to ensure that the window does not go beyond the last chunk of a document.
 * </p>
 * <p>By providing a {@link RAGObserver} to the constructor, the window chunkIds and window texts are added to the
 * observer.</p>
 */
public class WindowRetrievalStrategy implements RetrievalStrategy {

    private final Retriever retriever;
    private final int windowSize;

    public WindowRetrievalStrategy(Retriever retriever, int windowSize) {
        this.retriever = retriever;
        this.windowSize = windowSize;
    }

    @Override
    public RetrievalOutput retrieve(String question, int maxResults) {
        return retrieve(question, maxResults, false);
    }

    @Override
    public RetrievalOutput retrieve(String question, List<Float> vector, int maxResults) {
        return retrieve(question, vector, maxResults, false);
    }

    @Override
    public RetrievalOutput retrieve(String question, int maxResults, boolean observe) {
        List<RelevantChunk> relevantItems = retriever.findRelevantChunks(question, maxResults);

        return extractWindowFromRelevantChunk(relevantItems, observe);
    }

    @Override
    public RetrievalOutput retrieve(String question, List<Float> vector, int maxResults, boolean observe) {
        List<RelevantChunk> relevantItems = retriever.findRelevantChunks(question, vector, maxResults);

        return extractWindowFromRelevantChunk(relevantItems, observe);
    }

    private RetrievalOutput extractWindowFromRelevantChunk(List<RelevantChunk> relevantItems, boolean observe) {
        List<RetrievalOutput.RetrievalOutputItem> outputItems = relevantItems
                .stream()
                .map((RelevantChunk relevantChunk) -> getWindowChunksByRelevantChunk(relevantChunk, observe))
                .collect(Collectors.toList());

        return RetrievalOutput.builder().items(outputItems).build();
    }

    private RetrievalOutput.RetrievalOutputItem getWindowChunksByRelevantChunk(RelevantChunk relevantChunk, boolean observe) {
        List<String> chunkIds = getChunkIds(relevantChunk.getChunkId(), windowSize, relevantChunk.getTotalChunks());
        String text = combineChunksTexts(relevantChunk, chunkIds);

        if (observe) {
            RAGTracker.addWindowToChunkIds(
                    relevantChunk.getDocumentChunkId(),
                    chunkIds
            );
            RAGTracker.addWindowText(relevantChunk.getDocumentChunkId(), text);
        }
        return RetrievalOutput.RetrievalOutputItem.builder()
                .documentId(relevantChunk.getDocumentId())
                .chunkId(relevantChunk.getChunkId())
                .text(text.trim())
                .build();
    }

    private String combineChunksTexts(RelevantChunk relevantChunk, List<String> chunkIds) {
        return chunkIds.stream()
                .map(chunkId -> retriever.getChunk(relevantChunk.getDocumentId(), chunkId))
                .map(Chunk::getText)
                .collect(Collectors.joining(" "));
    }

    /**
     * Returns an array of chunkIds that are within the window size of the chunkId. The window size is the number of
     * chunks before and after the chunkId. The window size is limited by the number of chunks in the document. The
     * chunkId itself is also included in the array and starts with 0.
     * @param chunkId The chunkId for which the window is calculated
     * @param windowSize The window size
     * @param numberOfChunks The total number of chunks in the document
     * @return An array of chunkIds
     */
    static List<String> getChunkIds(String chunkId, int windowSize, int numberOfChunks) {
        // The chunkId has the format 0 or 0_0, we need to extract the last part
        String[] parts = chunkId.split("_");
        String lastPart = parts[parts.length - 1];
        int subChunkId = Integer.parseInt(lastPart);

        // Calculate the start and end of the window
        int start = Math.max(0, subChunkId - windowSize);
        int end = Math.min(numberOfChunks-1, subChunkId + windowSize);

        // Generate the list of chunkIds
        List<String> chunkIds = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            chunkIds.add(String.valueOf(i));
        }

        return chunkIds;
    }
}
