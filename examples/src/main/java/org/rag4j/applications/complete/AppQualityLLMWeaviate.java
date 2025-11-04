package org.rag4j.applications.complete;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.integrations.openai.OpenAIChatService;
import org.rag4j.integrations.openai.OpenAIConstants;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.integrations.weaviate.WeaviateAccess;
import org.rag4j.integrations.weaviate.retrieval.WeaviateRetriever;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.ObservedAnswerGenerator;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.generation.quality.AnswerQuality;
import org.rag4j.rag.generation.quality.AnswerQualityService;
import org.rag4j.rag.retrieval.ObservedRetriever;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.rag.retrieval.strategies.WindowRetrievalStrategy;
import org.rag4j.rag.tracker.LoggingRAGObserverPersistor;
import org.rag4j.rag.tracker.RAGObserver;
import org.rag4j.rag.tracker.RAGObserverPersistor;
import org.rag4j.rag.tracker.RAGTracker;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.List;

public class AppQualityLLMWeaviate {

    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Embedder embedder = new OpenAIEmbedder(openAIClient, OpenAIConstants.DEFAULT_EMBEDDING);

        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder);
        ObservedRetriever observedRetriever = new ObservedRetriever(retriever);
        WindowRetrievalStrategy windowRetrievalStrategy = new WindowRetrievalStrategy(observedRetriever, 1);

        List<String> exampleSentences = List.of(
                "How many bolts were replaced?",
                "Since When could people visit the Vasa?",
                "Since when was the Vasa available for the public to visit?",
                "Who was responsible for building the Vasa ship?",
                "Where did the person responsible for building the Vasa ship come from?"
        );

        List<AnswerQuality> overallQuality = exampleSentences.stream().map(question -> {
            RetrievalOutput retrievalOutput = windowRetrievalStrategy.retrieve(question, embedder.embed(question), 1, true);

            OpenAIClient client = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
            ChatService chatService = new OpenAIChatService(client);

            ObservedAnswerGenerator observedAnswerGenerator = new ObservedAnswerGenerator(chatService);
            String answer = observedAnswerGenerator.generateAnswer(question, retrievalOutput.constructContext());
            System.out.printf("Question: %s%nAnswer: %s%n", question, answer);
            System.out.printf("Context: %s%n", retrievalOutput.constructContext());

            RAGObserver observer = RAGTracker.getRAGObserver();
            RAGTracker.cleanup();

            RAGObserverPersistor persistor = new LoggingRAGObserverPersistor();
            persistor.persist(observer);

            AnswerQualityService answerQuality = new AnswerQualityService(chatService);
            AnswerQuality quality = answerQuality.determineQualityOfAnswer(observer);
            System.out.printf("Quality of answer compared to the question: %d, Reason: %s%n",
                    quality.getAnswerToQuestionQuality().getQuality(), quality.getAnswerToQuestionQuality().getReason());
            System.out.printf("Quality of answer coming from the context: %d, Reason %s%n",
                    quality.getAnswerFromContextQuality().getQuality(), quality.getAnswerFromContextQuality().getReason());

            return quality;
        }).toList();

        System.out.printf("Overall quality answer to question: %.3f%n",
                overallQuality.stream().mapToInt(answer -> answer.getAnswerToQuestionQuality().getQuality())
                        .average().orElse(0));
        System.out.printf("Overall quality answer from context: %.3f%n",
                overallQuality.stream().mapToInt(answer -> answer.getAnswerFromContextQuality().getQuality())
                        .average().orElse(0));
    }
}
