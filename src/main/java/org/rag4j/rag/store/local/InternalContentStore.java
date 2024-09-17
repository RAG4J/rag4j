package org.rag4j.rag.store.local;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.store.ContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.retrieval.ChunkProcessor;
import org.rag4j.rag.retrieval.Retriever;

import java.util.*;

/**
 * Mainly for demo purposes, it does not keep the data in a persistent storage. It uses the {@link EuclideanDistance}
 * to calculate the distance between the vectors of the chunks and the question. The embedder is used during indexing
 * and finding to create vector representations from the provided texts.
 */
public class InternalContentStore implements ContentStore, Retriever {
    private final Map<String, List<Float>> vectorStore;
    private final Map<String, Chunk> dataStore;

    private final Embedder embedder;

    public InternalContentStore(Embedder embedder) {
        this.embedder = embedder;
        this.vectorStore = new HashMap<>();
        this.dataStore = new HashMap<>();
    }

    @Override
    public void store(List<Chunk> chunks) {
        chunks.forEach(chunk -> {
            String key = extractKey(chunk.getDocumentId(), chunk.getChunkId());
            String text = chunk.getText();
            List<Float> vector = embedder.embed(text);
            this.vectorStore.put(key, vector);
            this.dataStore.put(key, chunk);
        });
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, int maxResults) {
        return this.findRelevantChunks(question, this.embedder.embed(question), maxResults);
    }

    @Override
    public List<RelevantChunk> findRelevantChunks(String question, List<Float> vector, int maxResults) {
        EuclideanDistance distanceCalculator = new EuclideanDistance();
        List<RelevantChunk> relevantChunks = new ArrayList<>();

        for (Map.Entry<String, List<Float>> entry : this.vectorStore.entrySet()) {
            double distance = distanceCalculator.compute(listToArrayWithDouble(vector), listToArrayWithDouble(entry.getValue()));
            Chunk chunk = this.dataStore.get(entry.getKey());
            relevantChunks.add(new RelevantChunk(chunk, distance));
        }

        relevantChunks.sort(Comparator.comparingDouble(RelevantChunk::getScore));

        return relevantChunks.subList(0, Math.min(maxResults, relevantChunks.size()));
    }

    @Override
    public Chunk getChunk(String documentId, String chunkId) {
        return this.dataStore.get(extractKey(documentId, chunkId));
    }

    @Override
    public void loopOverChunks(ChunkProcessor chunkProcessor) {
        this.dataStore.values().forEach(chunkProcessor::process);
    }

    private static double[] listToArrayWithDouble(List<Float> vector) {
        return vector.stream().mapToDouble(Float::doubleValue).toArray();
    }

    private static String extractKey(String documentId, String chunkId) {
        return String.format("%s_%s", documentId, chunkId);
    }
}
