package org.rag4j.rag.retrieval.quality;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.retrieval.Retriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * This class is used to generate questions for a given text and to evaluate the quality of the retrieval.
 */
public class RetrievalQualityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrievalQualityService.class);

    private final Retriever retriever;

    public RetrievalQualityService(Retriever retriever) {
        this.retriever = retriever;
    }

    public RetrievalQuality obtainRetrievalQuality(List<QuestionAnswerRecord> questionAnswerRecords, Embedder embedder) {
        Set<String> correct = new HashSet<>();
        Set<String> incorrect = new HashSet<>();
        questionAnswerRecords.forEach(questionAnswerRecord -> {
            String question = questionAnswerRecord.getQuestion();
            List<Float> embed = embedder.embed(question);
            RelevantChunk relevantChunks = this.retriever.findRelevantChunks(question, embed, 1).getFirst();
            if (relevantChunks.getChunkId().equals(questionAnswerRecord.getChunkId()) && relevantChunks.getDocumentId().equals(questionAnswerRecord.getDocumentId())) {
                correct.add(relevantChunks.getDocumentChunkId());
            } else {
                incorrect.add(questionAnswerRecord.getDocumentId() + "_" + questionAnswerRecord.getChunkId());
            }
        });

        return new RetrievalQuality(correct, incorrect);
    }


    public List<QuestionAnswerRecord> readQuestionAnswersFromFile(String fileName) {
        List<QuestionAnswerRecord> questionAnswerRecords = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(fileName)), StandardCharsets.UTF_8))) {
            readQuestionAnswerRecords(reader, questionAnswerRecords);
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading the file.", e);
            throw new RuntimeException(e);
        }
        return questionAnswerRecords;
    }

    public List<QuestionAnswerRecord> readQuestionAnswersFromFilePath(Path filePath, boolean deleteAfterRead) {
        List<QuestionAnswerRecord> questionAnswerRecords = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8))) {
            readQuestionAnswerRecords(reader, questionAnswerRecords);
            if (deleteAfterRead) {
                cleanupTempFile(filePath);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading the file.", e);
            throw new RuntimeException(e);
        }
        return questionAnswerRecords;
    }

    private static void readQuestionAnswerRecords(Reader reader, List<QuestionAnswerRecord> questionAnswerRecords) throws IOException {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader("document", "chunk", "text", "question")
                .setSkipHeaderRecord(true)
                .build();
        Iterable<CSVRecord> records = csvFormat.parse(reader);
        for (CSVRecord csvRecord : records) {
            String documentId = csvRecord.get("document");
            String chunkId = csvRecord.get("chunk");
            String text = csvRecord.get("text");
            String question = csvRecord.get("question");
            questionAnswerRecords.add(new QuestionAnswerRecord(documentId, chunkId, text, question));
        }
    }



    private static void cleanupTempFile(Path filePath) {
        try {
            // Check if the file exists
            if (Files.exists(filePath)) {
                // Delete the file
                Files.delete(filePath);
                LOGGER.debug("File deleted: {}", filePath);
            } else {
                LOGGER.debug("File not found: {}", filePath);
                return; // If the file doesn't exist, we can't delete the directory
            }

            // Get the parent directory
            Path parentDir = filePath.getParent();
            if (parentDir != null && Files.isDirectory(parentDir)) {
                // Check if the directory is empty
                if (isDirectoryEmpty(parentDir)) {
                    // Delete the directory
                    Files.delete(parentDir);
                    LOGGER.debug("Directory deleted: {}", parentDir);
                } else {
                    LOGGER.debug("Directory is not empty, not deleting: {}", parentDir);
                }
            } else {
                LOGGER.info("Parent directory not found or is not a directory: {}", parentDir);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // Helper method to check if a directory is empty
    public static boolean isDirectoryEmpty(Path directory) throws IOException {
        try (var dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext(); // Returns true if directory is empty
        }
    }
}
