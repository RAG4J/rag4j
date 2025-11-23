package org.rag4j.applications.integration;

import com.openai.client.OpenAIClient;
import org.rag4j.integration.openai.OpenAIConstants;
import org.rag4j.integration.openai.OpenAIEmbedder;
import org.rag4j.integration.openai.OpenAIFactory;
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
