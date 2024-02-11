package org.rag4j.integrations.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.CompletionsUsage;
import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.util.keyloader.KeyLoader;
import org.slf4j.Logger;

public class OpenAIChatService implements ChatService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpenAIChatService.class);
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

    public String askForResponse(ChatPrompt prompt) {
        ChatCompletionsOptions options = OpenAIPrompt.constructPrompt(prompt, 1.0);

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, options);

        CompletionsUsage usage = chatCompletions.getUsage();
        LOGGER.info("Usage: number of prompt token is {}, "
                        + "number of completion token is {}, and number of total tokens in request and response is {}.",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }
}
