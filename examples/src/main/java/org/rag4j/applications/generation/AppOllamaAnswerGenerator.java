package org.rag4j.applications.generation;

import org.rag4j.integrations.ollama.OllamaAccess;
import org.rag4j.integrations.ollama.OllamaChatService;

public class AppOllamaAnswerGenerator {
    public static void main(String[] args) {
        // initialize Ollama
        OllamaAccess ollama = new OllamaAccess();
        OllamaChatService ollamaChatService = new OllamaChatService(ollama);

        AppAnswerGenerator.execute(ollamaChatService);
    }
}
