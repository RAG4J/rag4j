package org.rag4j.applications.integration;

import com.azure.ai.openai.OpenAIClient;
import org.rag4j.integrations.openai.OpenAIConstants;
import org.rag4j.integrations.openai.OpenAIEmbedder;
import org.rag4j.integrations.openai.OpenAIFactory;
import org.rag4j.util.keyloader.KeyLoader;

public class AppOpenAIInfo {
    public static void main(String[] args) {
        // Load the different api keys from the environment
        KeyLoader keyLoader = new KeyLoader();

        // initialize OpenAI
        OpenAIClient openAIClient = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        OpenAIEmbedder openAIEmbedder = new OpenAIEmbedder(openAIClient, OpenAIConstants.DEFAULT_EMBEDDING);

        System.out.println(openAIEmbedder.embed("This is a test"));
    }
}
