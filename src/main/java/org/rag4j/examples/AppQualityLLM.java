package org.rag4j.examples;

import org.rag4j.domain.RetrievalOutput;
import org.rag4j.generation.AnswerGenerator;
import org.rag4j.generation.ObservedAnswerGenerator;
import org.rag4j.indexing.Embedder;
import org.rag4j.openai.OpenAIAnswerGenerator;
import org.rag4j.openai.OpenAIConstants;
import org.rag4j.openai.OpenAIEmbedder;
import org.rag4j.openai.OpenAIFactory;
import org.rag4j.quality.AnswerQualityService;
import org.rag4j.quality.AnswerQuality;
import org.rag4j.retrieval.ObservedRetriever;
import org.rag4j.retrieval.Retriever;
import org.rag4j.retrieval.WindowRetrievalStrategy;
import org.rag4j.tracker.LoggingRAGObserverPersistor;
import org.rag4j.tracker.RAGObserver;
import org.rag4j.tracker.RAGObserverPersistor;
import org.rag4j.tracker.RAGTracker;
import org.rag4j.util.KeyLoader;
import org.rag4j.weaviate.WeaviateAccess;
import org.rag4j.weaviate.retrieval.WeaviateRetriever;

import java.util.List;

public class AppQualityLLM {

    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Embedder embedder = new OpenAIEmbedder(keyLoader);

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

            AnswerGenerator answerGenerator = new OpenAIAnswerGenerator(keyLoader, OpenAIConstants.DEFAULT_MODEL);
            ObservedAnswerGenerator observedAnswerGenerator = new ObservedAnswerGenerator(answerGenerator);
            String answer = observedAnswerGenerator.generateAnswer(question, retrievalOutput.constructContext());
            System.out.printf("Question: %s%nAnswer: %s%n", question, answer);

            RAGObserver observer = RAGTracker.getRAGObserver();
            RAGTracker.cleanup();

            RAGObserverPersistor persistor = new LoggingRAGObserverPersistor();
            persistor.persist(observer);

            AnswerQualityService answerQuality = new AnswerQualityService(OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey()));
            AnswerQuality quality = answerQuality.determineQualityOfAnswer(observer);
            System.out.printf("Quality of answer compared to the question: %d%n",
                    quality.getAnswerToQuestionQuality().getQuality());
            System.out.printf("Quality of answer coming from the context: %d%n",
                    quality.getAnswerFromContextQuality().getQuality());

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
