package org.rag4j.applications.generation;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.applications.indexing.VasaContentReader;
import org.rag4j.integrations.openai.OpenAIChatService;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.rag.generation.QuestionGenerator;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.splitters.SentenceSplitter;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.rag.generation.QuestionGeneratorService;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.store.local.InternalContentStore;
import org.rag4j.util.keyloader.KeyLoader;

/**
 * Generates a question for each chunk in the Vasa content store. Beware this does a sygnificatly amount of calls to
 * the OpenAI API.
 */
public class AppQuestionGeneratorVasa {
    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        Embedder embedder = new OpenAIEmbedder(openAIClient);

        // Initialise the content store with all the vase chunks taken from the file vasa-timeline.jsonl
        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new SentenceSplitter());

        // Write the generated questions to a file
        ChatService chatService = new OpenAIChatService(openAIClient);
        QuestionGenerator questionGenerator = new QuestionGenerator(chatService);
        QuestionGeneratorService questionGeneratorService = new QuestionGeneratorService(contentStore, questionGenerator);
        questionGeneratorService.generateQuestionAnswerPairs("vase_questions_answers.txt");
    }
}
