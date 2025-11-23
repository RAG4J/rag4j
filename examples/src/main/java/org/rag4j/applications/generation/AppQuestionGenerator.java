package org.rag4j.applications.generation;

import com.openai.client.OpenAIClient;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.splitters.SingleChunkSplitter;
import org.rag4j.integration.openai.OpenAIChatService;
import org.rag4j.integration.openai.OpenAIEmbedder;
import org.rag4j.integration.openai.OpenAIFactory;
import org.rag4j.local.store.InternalContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.QuestionGenerator;
import org.rag4j.rag.generation.QuestionGeneratorService;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.util.keyloader.KeyLoader;

/**
 * Generates a question for a piece of text using an LLM and uses the retrieval quality service to evaluate the
 * quality of the retrieval.
 */
public class AppQuestionGenerator {

    public static void execute(Embedder embedder,ChatService chatService) {
        // Index a document into the content store
        InputDocument inputDocument = InputDocument.builder()
                .documentId("vasa")
                .text("The Vasa was a Swedish warship built between 1626 and 1628. The ship foundered and sank after " +
                        "sailing about 1,300 m (1,400 yd) into its maiden voyage on 10 August 1628.")
                .build();

        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocument(inputDocument, new SingleChunkSplitter());

        // Generate questions for the indexed chunk, as we have just one, by looping over them and collecting the
        // questions using the processor.
        QuestionGenerator questionGenerator = new QuestionGenerator(chatService);
        QuestionGeneratorService questionGeneratorService = new QuestionGeneratorService(contentStore, questionGenerator);
        QuestionCollectorProcessor questionCollectorProcessor = new QuestionCollectorProcessor(questionGeneratorService);
        contentStore.loopOverChunks(questionCollectorProcessor);

        // Use the gathered questions and print the information
        questionCollectorProcessor.getQuestionAnswerRecords().forEach(questionAnswerRecord -> {
            System.out.printf("Question: %s%n", questionAnswerRecord.getQuestion());
            System.out.printf("Text: %s%n", questionAnswerRecord.getText());
            System.out.printf("Chunk id: %s%n", questionAnswerRecord.getChunkId());
            System.out.printf("Document id: %s%n", questionAnswerRecord.getDocumentId());
            System.out.println();
        });

    }

    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        Embedder embedder = new OpenAIEmbedder(openAIClient);
        ChatService chatService = new OpenAIChatService(OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey()));

        execute(embedder,chatService);
    }
}
