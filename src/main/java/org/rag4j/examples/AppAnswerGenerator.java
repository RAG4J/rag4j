package org.rag4j.examples;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.generation.AnswerGenerator;
import org.rag4j.generation.ObservedAnswerGenerator;
import org.rag4j.openai.OpenAIAnswerGenerator;
import org.rag4j.openai.OpenAIFactory;
import org.rag4j.quality.AnswerQuality;
import org.rag4j.quality.AnswerQualityService;
import org.rag4j.tracker.RAGObserver;
import org.rag4j.tracker.RAGTracker;
import org.rag4j.util.KeyLoader;

/**
 * Shows how to generate an answer to a question from a context using the OpenAI API. To keep things simple, the
 * question and context are hardcoded in this example.
 */
public class AppAnswerGenerator {
    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        AnswerGenerator answerGenerator = new OpenAIAnswerGenerator(keyLoader);

        String question = "Since when was the Vasa available for the public to visit?";
        String context = "By Friday 16 February 1962, the ship is ready to be displayed to the general public at the " +
                "newly-constructed Wasa Shipyard, where visitors can see Vasa while a team of conservators, " +
                "carpenters and other technicians work to preserve the ship.";

        String answer = answerGenerator.generateAnswer(question, context);
        System.out.printf("The question is: %s%n", question);
        System.out.printf("The answer is: %s%n", answer);

        // Now with the observer present
        ObservedAnswerGenerator observedAnswerGenerator = new ObservedAnswerGenerator(answerGenerator);
        String observedAnswer = observedAnswerGenerator.generateAnswer(question, context);

        RAGObserver ragObserver = RAGTracker.getRAGObserver();
        RAGTracker.cleanup();

        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        AnswerQualityService answerQualityService = new AnswerQualityService(openAIClient);
        AnswerQuality answerQuality = answerQualityService.determineQualityOfAnswer(ragObserver);

        System.out.printf("The quality of the answer in relation to the question is: %s%n", answerQuality.getAnswerToQuestionQuality().getQuality());
        System.out.printf("The reasoning is: %s%n", answerQuality.getAnswerToQuestionQuality().getReason());

        System.out.printf("The quality of the answer in relation to the context is: %s%n", answerQuality.getAnswerFromContextQuality().getQuality());
        System.out.printf("The reasoning is: %s%n", answerQuality.getAnswerFromContextQuality().getReason());
    }
}
