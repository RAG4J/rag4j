package org.rag4j.applications.generation;

import org.rag4j.integrations.ollama.OllamaAccess;
import org.rag4j.integrations.ollama.OllamaChatService;
import org.rag4j.integrations.ollama.OllamaEmbedder;
import org.rag4j.rag.embedding.Embedder;

public class AppOllamaQuestionGenerator {
    public static void main(String[] args) {
        // initialize Ollama
        OllamaAccess ollama = new OllamaAccess();
        Embedder embedder = new OllamaEmbedder(ollama);
        OllamaChatService chatService = new OllamaChatService(ollama);

        AppQuestionGenerator.execute(embedder, chatService);
    }
}
