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

    public List<QuestionAnswerRecord> readQuestionAnswersFromFilePath(Path filePath) {
        List<QuestionAnswerRecord> questionAnswerRecords = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8))) {
            readQuestionAnswerRecords(reader, questionAnswerRecords);
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
}
