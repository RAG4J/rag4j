package org.rag4j.applications.indexing;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.indexing.InputDocument;
import org.rag4j.indexing.splitters.SemanticSplitter;
import org.rag4j.integrations.openai.OpenAIChatService;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.generation.knowledge.Knowledge;
import org.rag4j.rag.generation.knowledge.KnowledgeExtractorService;
import org.rag4j.rag.model.Chunk;
import org.rag4j.util.keyloader.KeyLoader;

import java.util.List;

public class AppSemanticSpliterOpenAI {
    public static void execute(KnowledgeExtractorService knowledgeExtractorService) {
        SemanticSplitter semanticSplitter = new SemanticSplitter(knowledgeExtractorService);
        String context = "By Friday 16 February 1962, the ship is ready to be displayed to the general public at the " +
                "newly-constructed Wasa Shipyard, where visitors can see Vasa while a team of conservators, " +
                "carpenters and other technicians work to preserve the ship.";

        InputDocument inputDocument = InputDocument.builder()
                .documentId("doc1")
                .text(context)
                .build();

        List<Chunk> chunks = semanticSplitter.split(inputDocument);

        chunks.forEach(chunk -> {
            System.out.printf("The chunk id is: %s%n", chunk.getChunkId());
            System.out.printf("The chunk text is: %s%n", chunk.getText());
        });
    }

    public static void main(String[] args) {
        KeyLoader keyLoader = new KeyLoader();
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());

        ChatService chatService = new OpenAIChatService(openAIClient);
        KnowledgeExtractorService knowledgeExtractorService = new KnowledgeExtractorService(chatService);
        execute(knowledgeExtractorService);
    }
}
