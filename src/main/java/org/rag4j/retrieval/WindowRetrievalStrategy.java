package org.rag4j.retrieval;

import org.rag4j.domain.RelevantChunk;
import org.rag4j.domain.RetrievalOutput;
import org.rag4j.tracker.RAGObserver;
import org.rag4j.domain.Chunk;
import org.rag4j.tracker.RAGTracker;

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
    public RetrievalOutput retrieve(String question, List<Double> vector, int maxResults) {
        return retrieve(question, vector, maxResults, false);
    }

    @Override
    public RetrievalOutput retrieve(String question, int maxResults, boolean observe) {
        List<RelevantChunk> relevantItems = retriever.findRelevantChunks(question, maxResults);

        return extractWindowFromRelevantChunk(relevantItems, observe);
    }

    @Override
    public RetrievalOutput retrieve(String question, List<Double> vector, int maxResults, boolean observe) {
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
        int[] chunkIds = getChunkIds(relevantChunk.getChunkId(), windowSize, relevantChunk.getTotalChunks());
        String text = combineChunksTexts(relevantChunk, chunkIds);

        if (observe) {
            RAGTracker.addWindowToChunkIds(
                    relevantChunk.getDocumentChunkId(),
                    Arrays.stream(chunkIds).boxed().collect(Collectors.toList())
            );
            RAGTracker.addWindowText(relevantChunk.getDocumentChunkId(), text);
        }
        return RetrievalOutput.RetrievalOutputItem.builder()
                .documentId(relevantChunk.getDocumentId())
                .chunkId(relevantChunk.getChunkId())
                .text(text.trim())
                .build();
    }

    private String combineChunksTexts(RelevantChunk relevantChunk, int[] chunkIds) {
        return Arrays.stream(chunkIds)
                .mapToObj(chunkId -> retriever.getChunk(relevantChunk.getDocumentId(), chunkId))
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
    static int[] getChunkIds(int chunkId, int windowSize, int numberOfChunks) {

        // Calculate the start and end of the window
        int start = Math.max(0, chunkId - windowSize);
        int end = Math.min(numberOfChunks-1, chunkId + windowSize);

        // Generate the array of chunkIds
        int[] chunkIds = new int[end - start + 1];
        for (int i = 0; i < chunkIds.length; i++) {
            chunkIds[i] = start + i;
        }

        return chunkIds;
    }
}
