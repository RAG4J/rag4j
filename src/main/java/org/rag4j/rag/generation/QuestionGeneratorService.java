package org.rag4j.rag.generation;

import org.rag4j.rag.retrieval.Retriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QuestionGeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionGeneratorService.class);

    private final Retriever retriever;
    private final QuestionGenerator questionGenerator;

    public QuestionGeneratorService(Retriever retriever, QuestionGenerator questionGenerator) {
        this.retriever = retriever;
        this.questionGenerator = questionGenerator;
    }

    public void generateQuestionAnswerPairs(String fileName) {
        String directory = System.getProperty("user.dir") + "/src/main/resources/data"; // get current working directory
        Path filePath = Paths.get(directory, fileName);
        writeFile(filePath);
    }

    public Path generateQuestionAnswerPairsAndSaveToTempFile(String fileName) {
        try {
            // Create a temporary directory
            Path tempDir = Files.createTempDirectory("questionAnswerPairs");
            Path filePath = tempDir.resolve(fileName); // Resolve the file path in the temp directory
            writeFile(filePath);
            return filePath;
        } catch (IOException e) {
            LOGGER.error("An error occurred while creating the temporary directory.", e);
            throw new RuntimeException(e);
        }
    }

    private void writeFile(Path filePath) {
        try (PrintWriter writer = new PrintWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            writer.println("document,chunk,text,question");
            retriever.loopOverChunks(chunk -> {
                // Use LLM to generate a question for this chunk
                String question = generateQuestion(chunk.getText());

                // Write the question and answer to the file
                String documentId = chunk.getDocumentId();
                String chunkId = String.valueOf(chunk.getChunkId());
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n", documentId, chunkId, chunk.getText(), question);
                LOGGER.info("Generated question: {}", question);
            });
        } catch (IOException e) {
            LOGGER.error("An error occurred while writing to the file.", e);
            throw new RuntimeException(e);
        }
    }

    public String generateQuestion(String text) {
        return this.questionGenerator.generateQuestion(text);
    }

}
