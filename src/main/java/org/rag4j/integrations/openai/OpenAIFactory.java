package org.rag4j.integrations.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;

/**
 * Factory for obtaining an {@link OpenAIClient}.
 */
public class OpenAIFactory {
    public static OpenAIClient obtainsClient(String apiKey) {
        return new OpenAIClientBuilder()
                .credential(new KeyCredential(apiKey))
                .buildClient();
    }

}
