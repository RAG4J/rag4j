package org.rag4j.applications.retrieval;

import com.openai.client.OpenAIClient;
import org.rag4j.integration.openai.OpenAIChatService;
import org.rag4j.integration.openai.OpenAIEmbedder;
import org.rag4j.integration.openai.OpenAIFactory;
import org.rag4j.integration.weaviate.WeaviateAccess;
import org.rag4j.integration.weaviate.retrieval.WeaviateRetriever;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.answer.AnswerGenerator;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.RetrievalStrategy;
import org.rag4j.rag.retrieval.Retriever;
import org.rag4j.rag.retrieval.strategies.DocumentRetrievalStrategy;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.List;

public class AppDocumentRetrievalStrategyWeaviate {

    public static void main(String[] args) {

        // Now we use Weaviate to retrieve the document
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        WeaviateAccess weaviateAccess = new WeaviateAccess(keyLoader);
        Embedder embedder = new OpenAIEmbedder(openAIClient);
        List<String> additionalFields = List.of("title", "time", "room", "speakers", "tags");
        Retriever retriever = new WeaviateRetriever(weaviateAccess, embedder, true, additionalFields);

        String question = "What are jettro and daniel talking about?";

        RetrievalStrategy retrievalStrategy = new DocumentRetrievalStrategy(retriever);
        RetrievalOutput retrievalOutput = retrievalStrategy.retrieve(question, 1);
        AnswerGenerator answerGenerator = new AnswerGenerator(new OpenAIChatService(openAIClient));

        String answer = answerGenerator.generateAnswer(question, retrievalOutput.constructContext());

        System.out.printf("Answer: %s%n", answer);

    }
}
