package org.rag4j.applications.integration;

import org.rag4j.integrations.ollama.OllamaAccess;

public class AppOllamaModels {
    public static void main(String[] args) {
        // initialize Ollama
        OllamaAccess ollama = new OllamaAccess();

        ollama.listModels().forEach(System.out::println);
    }
}
