package org.rag4j.rag.retrieval.strategies;


import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.RetrievalStrategy;
import org.rag4j.rag.retrieval.Retriever;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HierarchicalRetrievalStrategy implements RetrievalStrategy {
    private final Retriever retriever;
    private final int maxLevels;

    public HierarchicalRetrievalStrategy(Retriever retriever, int maxLevels) {
        this.retriever = retriever;
        this.maxLevels = maxLevels;
    }

    @Override
    public RetrievalOutput retrieve(String question, int maxResults) {
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks(question, maxResults);
        return extractHierarchyForChunks(relevantChunks);
    }

    @Override
    public RetrievalOutput retrieve(String question, List<Float> vector, int maxResults) {
        List<RelevantChunk> relevantChunks = retriever.findRelevantChunks(question, vector, maxResults);
        return extractHierarchyForChunks(relevantChunks);
    }

    private RetrievalOutput extractHierarchyForChunks(List<RelevantChunk> relevantChunks) {
        List<RetrievalOutput.RetrievalOutputItem> retrievalOutputItems = new ArrayList<>();
        Set<String> usedChunkIds = new HashSet<>();

        for (RelevantChunk relevantChunk : relevantChunks) {
            String hierarchicalChunkId = chunkIdForHierarchy(relevantChunk.getChunkId(), maxLevels);

            if (usedChunkIds.contains(hierarchicalChunkId)) {
                continue;
            }

            Chunk hierarchicalChunk = retriever.getChunk(relevantChunk.getDocumentId(), hierarchicalChunkId);
            usedChunkIds.add(hierarchicalChunkId);
            RetrievalOutput.RetrievalOutputItem relevantItem = RetrievalOutput.RetrievalOutputItem.builder()
                    .documentId(relevantChunk.getDocumentId())
                    .chunkId(relevantChunk.getChunkId())
                    .text(hierarchicalChunk.getText())
                    .build();
            retrievalOutputItems.add(relevantItem);
        }

        return RetrievalOutput.builder().items(retrievalOutputItems).build();
    }

    private static String chunkIdForHierarchy(String chunkId, int maxLevels) {
        String[] hierarchicalChunkIds = chunkId.split("_");

        int maxLevelsToGoUp = Math.min(maxLevels, hierarchicalChunkIds.length - 1);
        int toKeep = hierarchicalChunkIds.length - maxLevelsToGoUp;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < toKeep; i++) {
            if (i > 0) {
                result.append("_");
            }
            result.append(hierarchicalChunkIds[i]);
        }

        return result.toString();
    }
}