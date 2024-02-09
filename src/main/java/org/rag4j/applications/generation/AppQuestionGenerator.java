package org.rag4j.applications.generation;

import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.InputDocument;
import org.rag4j.rag.generation.QuestionGenerator;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.indexing.splitters.SingleChunkSplitter;
import org.rag4j.integrations.openai.OpenAIConstants;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.integrations.openai.OpenAIQuestionGenerator;
import org.rag4j.rag.generation.QuestionGeneratorService;
import org.rag4j.rag.retrieval.quality.RetrievalQuality;
import org.rag4j.rag.retrieval.quality.RetrievalQualityService;
import org.rag4j.rag.store.local.InternalContentStore;
import org.rag4j.util.KeyLoader;

/**
 * Generates a question for a piece of text using an LLM and uses the retrieval quality service to evaluate the
 * quality of the retrieval.
 */
public class AppQuestionGenerator {

    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        Embedder embedder = new OpenAIEmbedder(keyLoader);

        InputDocument inputDocument = InputDocument.builder()
                .documentId("vasa")
                .text("The Vasa was a Swedish warship built between 1626 and 1628. The ship foundered and sank after " +
                        "sailing about 1,300 m (1,400 yd) into its maiden voyage on 10 August 1628.")
                .build();

        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocument(inputDocument, new SingleChunkSplitter());

        QuestionGenerator questionGenerator = new OpenAIQuestionGenerator(keyLoader, OpenAIConstants.DEFAULT_MODEL);
        QuestionGeneratorService questionGeneratorService = new QuestionGeneratorService(contentStore, questionGenerator);
        QuestionCollectorProcessor questionCollectorProcessor = new QuestionCollectorProcessor(questionGeneratorService);
        contentStore.loopOverChunks(questionCollectorProcessor);

        questionCollectorProcessor.getQuestionAnswerRecords().forEach(questionAnswerRecord -> {
            System.out.printf("Question: %s%n", questionAnswerRecord.getQuestion());
            System.out.printf("Text: %s%n", questionAnswerRecord.getText());
            System.out.printf("Chunk id: %s%n", questionAnswerRecord.getChunkId());
            System.out.printf("Document id: %s%n", questionAnswerRecord.getDocumentId());
            System.out.println();
        });

        RetrievalQualityService retrievalQualityService = new RetrievalQualityService(contentStore);
        RetrievalQuality retrievalQuality = retrievalQualityService.obtainRetrievalQuality(
                questionCollectorProcessor.getQuestionAnswerRecords(), embedder);

        System.out.printf("Precision: %.3f%n", retrievalQuality.getPrecision());
        System.out.printf("Total items: %d%n", retrievalQuality.totalItems());
    }
}
