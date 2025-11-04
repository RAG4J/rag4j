package org.rag4j.applications.retrieval;

import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.Splitter;
import org.rag4j.indexing.SplitterChain;
import org.rag4j.indexing.splitters.SectionSplitter;
import org.rag4j.indexing.splitters.SentenceSplitter;
import org.rag4j.integrations.ollama.OllamaAccess;
import org.rag4j.integrations.ollama.OllamaChatService;
import org.rag4j.integrations.ollama.OllamaEmbedder;
import org.rag4j.rag.embedding.Embedder;
import org.rag4j.rag.generation.AnswerGenerator;
import org.rag4j.rag.model.Chunk;
import org.rag4j.rag.model.RelevantChunk;
import org.rag4j.rag.retrieval.RetrievalOutput;
import org.rag4j.rag.retrieval.strategies.HierarchicalRetrievalStrategy;
import org.rag4j.rag.store.local.InternalContentStore;

import java.util.List;
import java.util.Map;

public class AppHierarchicalRetrievalStrategy {
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
        Splitter splitter = new SplitterChain(List.of(new SectionSplitter(), new SentenceSplitter()));

        // Store the document using the splitter
        InputDocument inputDocument = InputDocument.builder()
                .documentId("jettro-aws-haarlem-meetup-2024")
                .text("""
                        Large language models become more powerful every day. Each release adds more features, such as \
                        working with agents, pictures, code generation, and functions. One thing that will remain \
                        important is having access to knowledge and recent time-driven data. A now well-known pattern \
                        to overcome this problem is RAG or Retrieval Augmented Generation.
                        
                        This talk teaches you about the required components for a RAG-based system. An essential \
                        part is a search component, the retriever. For the retriever, you learn about Amazon \
                        OpenSearch Service’s capabilities. Together, we explore vector, lexical, and hybrid search. \
                        We use OpenAI to generate answers and embeddings for the large language model. The demo \
                        application uses Rag4p, a basic RAG system for learning the different RAG components.""")
                .properties(Map.of("title", "Gen AI needs high-quality and performant search.",
                                "presenters", List.of("Jettro Coenradie"),
                                "room", "De Koepel",
                                "time", "2024-07-04 19:00:00"
                        )
                )
                .build();
        List<Chunk> chunks = splitter.split(inputDocument);
        contentStore.store(chunks);

        // Find the most relevant chunk for the question
        List<RelevantChunk> relevantChunks = contentStore.findRelevantChunks(
                "Who is presenting a workshop about question-answer systems?", 1);
        System.out.println("Most relevant chunk for the question: " + relevantChunks.getFirst().getText());

//        String question = "Who is talking about RAG based systems?";

        String question = "What features do Large Language Model have?";

        AnswerGenerator answerGenerator = new AnswerGenerator(new OllamaChatService(ollamaAccess));

        HierarchicalRetrievalStrategy strategy = new HierarchicalRetrievalStrategy(contentStore, 1);
        RetrievalOutput retrieved = strategy.retrieve(question, 1);
        generateAnswer(question, retrieved, answerGenerator);
    }
}
