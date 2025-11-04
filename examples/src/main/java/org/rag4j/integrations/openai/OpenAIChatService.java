package org.rag4j.integrations.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsJsonResponseFormat;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.CompletionsUsage;
import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;
import org.slf4j.Logger;

import static org.rag4j.integrations.openai.OpenAIConstants.DEFAULT_MODEL;

public class OpenAIChatService implements ChatService {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpenAIChatService.class);
    private final String model;
    private final OpenAIClient client;

    public OpenAIChatService(OpenAIClient client) {
        this(client, DEFAULT_MODEL);
    }

    public OpenAIChatService(OpenAIClient client, String model) {
        this.model = model;
        this.client = client;
    }

    @Override
    public String askForResponse(ChatPrompt prompt) {
        ChatCompletionsOptions options = OpenAIPrompt.constructPrompt(prompt, 1.0);

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, options);

        CompletionsUsage usage = chatCompletions.getUsage();
        LOGGER.info("Usage: number of prompt token is {}, "
                        + "number of completion token is {}, and number of total tokens in request and response is {}.",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }

    @Override
    public String askForJsonResponse(ChatPrompt prompt) {
        ChatCompletionsOptions options = OpenAIPrompt.constructPrompt(prompt, 1.0);
        options.setResponseFormat(new ChatCompletionsJsonResponseFormat());

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, options);

        CompletionsUsage usage = chatCompletions.getUsage();
        LOGGER.info("Usage: number of prompt token is {}, "
                        + "number of completion token is {}, and number of total tokens in request and response is {}.",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());

        return chatCompletions.getChoices().getFirst().getMessage().getContent();

    }
}
