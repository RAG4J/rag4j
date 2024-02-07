package org.rag4j.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import org.rag4j.chat.ChatPrompt;
import org.rag4j.chat.ChatService;
import org.rag4j.util.KeyLoader;

public class OpenAIChatService implements ChatService {
    private final String model = OpenAIConstants.GPT4;
    private final OpenAIClient client;

    public OpenAIChatService() {
        this(new KeyLoader());
    }

    public OpenAIChatService(KeyLoader keyLoader) {
        this(OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey()));
    }

    public OpenAIChatService(OpenAIClient client) {
        this.client = client;
    }

    public String askForQuality(ChatPrompt prompt) {
        ChatCompletionsOptions options = OpenAIPrompt.constructPrompt(prompt, 1.0);

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, options);

        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }
}
