package org.rag4j.applications.knowledge;

import org.rag4j.integration.ollama.OllamaAccess;
import org.rag4j.integration.ollama.OllamaChatService;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.generation.knowledge.Knowledge;
import org.rag4j.rag.generation.knowledge.KnowledgeExtractorService;

import java.util.List;

public class AppKnowledgeExtractorOllama {
    public static void execute(ChatService chatService) {
        KnowledgeExtractorService knowledgeExtractorService = new KnowledgeExtractorService(chatService);
        String context = "By Friday 16 February 1962, the ship is ready to be displayed to the general public at the " +
                "newly-constructed Wasa Shipyard, where visitors can see Vasa while a team of conservators, " +
                "carpenters and other technicians work to preserve the ship.";
        List<Knowledge> knowledges = knowledgeExtractorService.extractKnowledge(context);

        knowledges.forEach(knowledge -> {
            System.out.printf("The knowledge is: %s%n", knowledge.getSubject());
            System.out.printf("The confidence is: %s%n", knowledge.getDescription());
        });
    }

    public static void main(String[] args) {
        OllamaAccess ollama = new OllamaAccess();

        ChatService chatService = new OllamaChatService(ollama);
        execute(chatService);
    }

}
