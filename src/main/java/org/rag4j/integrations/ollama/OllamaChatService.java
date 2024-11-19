package org.rag4j.integrations.ollama;

import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;

public class OllamaChatService implements ChatService {
    private final OllamaAccess ollama;
    private final String model;

    public OllamaChatService(OllamaAccess ollama) {
        this(ollama, OllamaConstants.DEFAULT_MODEL);
    }

    public OllamaChatService(OllamaAccess ollama, String model) {
        this.ollama = ollama;
        this.model = model;
    }

    @Override
    public String askForResponse(ChatPrompt prompt) {
        return generateResponse(prompt, false);
    }

    @Override
    public String askForJsonResponse(ChatPrompt prompt) {
        return generateResponse(prompt, true);
    }

    private String generateResponse(ChatPrompt prompt, boolean returnJson) {
        String stringPrompt = String.format("%s\n\n%s",prompt.createSystemMessage(), prompt.createUserMessage());
        return this.ollama.generateAnswer(stringPrompt, this.model, returnJson);
    }
}
