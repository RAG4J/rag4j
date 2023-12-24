package org.rag4j.examples;

import lombok.Getter;
import org.rag4j.domain.Chunk;
import org.rag4j.quality.QuestionAnswerRecord;
import org.rag4j.quality.QuestionGeneratorService;
import org.rag4j.retrieval.ChunkProcessor;

import java.util.ArrayList;
import java.util.List;

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
        QuestionAnswerRecord questionAnswerRecord = org.rag4j.quality.QuestionAnswerRecord.builder()
                .question(question)
                .text(chunk.getText())
                .chunkId(chunk.getChunkId())
                .documentId(chunk.getDocumentId())
                .build();
        questionAnswerRecords.add(questionAnswerRecord);
    }

}
