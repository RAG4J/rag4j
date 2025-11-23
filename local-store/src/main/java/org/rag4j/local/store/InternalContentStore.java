package org.rag4j.local.store;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.store.ContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.retrieval.ChunkProcessor;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.rag.store.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Mainly for demo purposes, it does not keep the data in a persistent storage. It uses the {@link EuclideanDistance}
 * to calculate the distance between the vectors of the chunks and the question. The embedder is used during indexing
 * and finding to create vector representations from the provided texts.
 */
public class InternalContentStore implements ContentStore, Retriever {
    private final static Logger LOGGER = LoggerFactory.getLogger(InternalContentStore.class);
    private HashMap<String, VectorChunk> dataStore;
    private Metadata metadata;

    private final Embedder embedder;

    public InternalContentStore(Embedder embedder) {
        this.embedder = embedder;
        this.dataStore = new HashMap<>();
        this.metadata = Metadata.builder()
                .name("InternalContentStore")
                .creationDate(new Date())
                .embedder(embedder.identifier())
                .supplier(embedder.supplier())
                .model(embedder.model())
                .build();
    }

    @Override
    public void store(List<Chunk> chunks) {
        chunks.forEach(chunk -> {
            String key = extractKey(chunk.getDocumentId(), chunk.getChunkId());
            String text = chunk.getText();
            List<Float> vector = embedder.embed(text);
            this.dataStore.put(key, new VectorChunk(chunk, vector));
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

        for (Map.Entry<String, VectorChunk> entry : this.dataStore.entrySet()) {
            List<Float> storedVector = entry.getValue().getVector();
            double distance = distanceCalculator.compute(
                    listToArrayWithDouble(vector),
                    listToArrayWithDouble(storedVector)
            );
            Chunk chunk = entry.getValue().getChunk();
            relevantChunks.add(new RelevantChunk(chunk, distance));
        }

        relevantChunks.sort(Comparator.comparingDouble(RelevantChunk::getScore));

        return relevantChunks.subList(0, Math.min(maxResults, relevantChunks.size()));
    }

    @Override
    public Chunk getChunk(String documentId, String chunkId) {
        return this.dataStore.get(extractKey(documentId, chunkId)).getChunk();
    }

    @Override
    public void loopOverChunks(ChunkProcessor chunkProcessor) {
        this.dataStore.values().stream().map(VectorChunk::getChunk).forEach(chunkProcessor::process);
    }

    private static double[] listToArrayWithDouble(List<Float> vector) {
        return vector.stream().mapToDouble(Float::doubleValue).toArray();
    }

    private static String extractKey(String documentId, String chunkId) {
        return String.format("%s_%s", documentId, chunkId);
    }

    public void backupToDisk(Path pathToFile, String fileName) {
        Kryo kryo = this.initKryo();

        String dataFileName = fileName + ".ser";
        String metadataFileName = fileName + "-metadata.ser";

        // Serializing the object
        try (Output output = new Output(new FileOutputStream(pathToFile.resolve(dataFileName).toFile()))) {
            kryo.writeObject(output, this.dataStore);
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the backup file for the data store.", e);
        }

        try (Output output = new Output(new FileOutputStream(pathToFile.resolve(metadataFileName).toFile()))) {
            kryo.writeObject(output, this.metadata);
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing the metadata file for the data store.", e);
        }

    }

    public void loadFromDisk(Path pathToFile, String fileName) {
        Kryo kryo = this.initKryo();

        String dataFileName = fileName + ".ser";
        String metadataFileName = fileName + "-metadata.ser";

        try (Input input = new Input(new FileInputStream(pathToFile.resolve(metadataFileName).toFile()))) {
            this.metadata = kryo.readObject(input, Metadata.class);
            LOGGER.info("Loaded metadata: {}", this.metadata);
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading the metadata file for the data store.", e);
        }

        // Check the embedder from the metadata and verify if it is the same as the one used during initialization
        if (!this.metadata.getEmbedder().equals(this.embedder.identifier())) {
            LOGGER.warn("The embedder used during initialization is different from the one used during loading.");
            throw new LoadBackupException(
                    String.format("The embedder used during initialization '%s' is different from the one used during loading '%s'.",
                            this.embedder.identifier(), this.metadata.getEmbedder())
            );
        }

        // Deserializing the object
        try (Input input = new Input(new FileInputStream(pathToFile.resolve(dataFileName).toFile()))) {
            this.dataStore = kryo.readObject(input, HashMap.class);
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading the backup file for the data store.", e);
        }

    }

    private Kryo initKryo() {
        Kryo kryo = new Kryo();
        kryo.register(HashMap.class);
        kryo.register(VectorChunk.class);
        kryo.register(Chunk.class);
        kryo.register(ArrayList.class);
        kryo.register(Metadata.class);
        kryo.register(Date.class);
        return kryo;
    }
}
