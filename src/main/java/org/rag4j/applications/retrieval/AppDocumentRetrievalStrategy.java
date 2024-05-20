package org.rag4j.applications.retrieval;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.indexing.splitters.SentenceSplitter;
import org.rag4j.integrations.ollama.OllamaAccess;
import org.rag4j.integrations.ollama.OllamaChatService;
import org.rag4j.integrations.ollama.OllamaEmbedder;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.AnswerGenerator;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.strategies.DocumentRetrievalStrategy;
import org.rag4j.rag.retrieval.strategies.WindowRetrievalStrategy;
import org.rag4j.rag.store.local.InternalContentStore;

import java.util.List;
import java.util.Map;

public class AppDocumentRetrievalStrategy {

    static void generateAnswer(String question, RetrievalOutput retrieved, AnswerGenerator answerGenerator) {
        String answer = answerGenerator.generateAnswer(question, retrieved.constructContext());
        System.out.println("--------------------------------------------------");
        System.out.printf("Question: %s%n", question);
        System.out.printf("Context: %s%n", retrieved.constructContext());
        System.out.printf("Answer: %s%n", answer);
    }

    public static void main(String[] args) {

        // prepare the content store, which we also use as a retriever
        OllamaAccess ollamaAccess = new OllamaAccess();
        Embedder embedder = new OllamaEmbedder(ollamaAccess);
        InternalContentStore contentStore = new InternalContentStore(embedder);
        Splitter splitter = new SentenceSplitter();

        // Store the document using the splitter
        InputDocument inputDocument = InputDocument.builder()
                .documentId("jettro-aws-haarlem-meetup-2024")
                .text("Large language models become more powerful every day. Each release adds more features, such as " +
                        "working with agents, pictures, code generation, and functions. One thing that will remain " +
                        "important is having access to knowledge and recent time-driven data. A now well-known pattern " +
                        "to overcome this problem is RAG or Retrieval Augmented Generation.\n" +
                        "This talk teaches you about the required components for a RAG-based system. An essential " +
                        "part is a search component, the retriever. For the retriever, you learn about Amazon " +
                        "OpenSearch Service’s capabilities. Together, we explore vector, lexical, and hybrid search. " +
                        "We use OpenAI to generate answers and embeddings for the large language model. The demo " +
                        "application uses Rag4p, a basic RAG system for learning the different RAG components.")
                .properties(Map.of("title", "Gen AI needs high-quality and performant search.",
                        "presenters", List.of("Jettro Coenradie"),
                        "room","De Koepel",
                        "time", "2024-07-04 19:00:00"
                        )
                )
                .build();
        contentStore.store(splitter.split(inputDocument));

        // Setup the retrieval part with two different retrieval strategies, use Ollama as LLM
        DocumentRetrievalStrategy documentRetrievalStrategy = new DocumentRetrievalStrategy(contentStore);
        WindowRetrievalStrategy windowRetrievalStrategy = new WindowRetrievalStrategy(contentStore, 2);
        AnswerGenerator answerGenerator = new AnswerGenerator(new OllamaChatService(ollamaAccess));

        // Ask the question and generate the answer using two different retrieval strategies
        String question = "Who is talking about RAG based systems?";

        RetrievalOutput windowRetrieved = windowRetrievalStrategy.retrieve(question, 1);
        generateAnswer(question, windowRetrieved, answerGenerator);

        RetrievalOutput documentRetrieved = documentRetrievalStrategy.retrieve(question, 1);
        generateAnswer(question, documentRetrieved, answerGenerator);

    }
}
