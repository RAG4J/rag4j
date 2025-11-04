package org.rag4j.integration.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

/**
 * Factory for obtaining an {@link OpenAIClient}.
 */
public class OpenAIFactory {
    public static OpenAIClient obtainsClient(String apiKey) {
        return new OpenAIOkHttpClient.Builder()
                .apiKey(apiKey)
                .build();
    }

}
