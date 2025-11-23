package org.rag4j.applications.complete;

import com.openai.client.OpenAIClient;
import org.rag4j.applications.indexing.VasaContentReader;
import org.rag4j.indexing.IndexingService;
import org.rag4j.indexing.splitters.MaxTokenSplitter;
import org.rag4j.integration.openai.OpenAIChatService;
import org.rag4j.integration.openai.OpenAIConstants;
import org.rag4j.integration.openai.OpenAIEmbedder;
import org.rag4j.integration.openai.OpenAIFactory;
import org.rag4j.local.store.InternalContentStore;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.answer.ObservedAnswerGenerator;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.generation.quality.AnswerQuality;
import org.rag4j.rag.generation.quality.AnswerQualityService;
import org.rag4j.rag.retrieval.ObservedRetriever;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.strategies.WindowRetrievalStrategy;
import org.rag4j.rag.tracker.LoggingRAGObserverPersistor;
import org.rag4j.rag.tracker.RAGObserver;
import org.rag4j.rag.tracker.RAGObserverPersistor;
import org.rag4j.rag.tracker.RAGTracker;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.List;

public class AppQualityLLMLocal {
    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        Embedder embedder = new OpenAIEmbedder(openAIClient, OpenAIConstants.DEFAULT_EMBEDDING);

        InternalContentStore contentStore = new InternalContentStore(embedder);
        IndexingService indexingService = new IndexingService(contentStore);
        indexingService.indexDocuments(new VasaContentReader(), new MaxTokenSplitter(200));

        ObservedRetriever observedRetriever = new ObservedRetriever(contentStore);
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

            ChatService chatService = new OpenAIChatService(OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey()));
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
