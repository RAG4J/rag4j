package org.rag4j.applications.generation;

import lombok.Getter;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.retrieval.quality.QuestionAnswerRecord;
import org.rag4j.rag.generation.QuestionGeneratorService;
import org.rag4j.rag.retrieval.ChunkProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Processor that generates a question for each chunk of text and collects the generated questions. It provides a list
 * of {@link QuestionAnswerRecord} that contains the question, the text and the chunk id.
 */
public class QuestionCollectorProcessor implements ChunkProcessor {

    private final QuestionGeneratorService questionGeneratorService;
    @Getter
    private final List<QuestionAnswerRecord> questionAnswerRecords;

    public QuestionCollectorProcessor(QuestionGeneratorService questionGeneratorService) {
        this.questionGeneratorService = questionGeneratorService;
        this.questionAnswerRecords = new ArrayList<>();
    }

    @Override
    public void process(Chunk chunk) {
        String question = questionGeneratorService.generateQuestion(chunk.getText());
        QuestionAnswerRecord questionAnswerRecord = QuestionAnswerRecord.builder()
                .question(question)
                .text(chunk.getText())
                .chunkId(chunk.getChunkId())
                .documentId(chunk.getDocumentId())
                .build();
        questionAnswerRecords.add(questionAnswerRecord);
    }

}
