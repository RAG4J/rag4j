package org.rag4j.applications.knowledge;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.integrations.ollama.OllamaAccess;
import org.rag4j.integrations.ollama.OllamaChatService;
import org.rag4j.integrations.openai.OpenAIChatService;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.generation.knowledge.Knowledge;
import org.rag4j.rag.generation.knowledge.KnowledgeExtractorService;
import org.rag4j.util.keyloader.KeyLoader;

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
