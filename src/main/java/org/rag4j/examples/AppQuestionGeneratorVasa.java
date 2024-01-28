package org.rag4j.examples;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.generation.QuestionGenerator;
import org.rag4j.indexing.Embedder;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.OpenNLPSentenceSplitter;
import org.rag4j.openai.OpenAIConstants;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.openai.OpenAIFactory;
import org.rag4j.openai.OpenAIQuestionGenerator;
import org.rag4j.quality.QuestionGeneratorService;
import org.rag4j.store.InternalContentStore;
import org.rag4j.util.KeyLoader;

/**
 * Generates a question for each chunk in the Vasa content store. Beware this does a sygnificatly amount of calls to
 * the OpenAI API.
 */
public class AppQuestionGeneratorVasa {
    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        Embedder embedder = new OpenAIEmbedder(keyLoader);

        // Initialise the content store with all the vase chunks taken from the file vasa-timeline.jsonl
        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new OpenNLPSentenceSplitter());

        // Write the generated questions to a file
        QuestionGenerator questionGenerator = new OpenAIQuestionGenerator(keyLoader, OpenAIConstants.DEFAULT_MODEL);
        QuestionGeneratorService questionGeneratorService = new QuestionGeneratorService(contentStore, questionGenerator);
        questionGeneratorService.generateQuestionAnswerPairs("vase_questions_answers.txt");
    }
}
