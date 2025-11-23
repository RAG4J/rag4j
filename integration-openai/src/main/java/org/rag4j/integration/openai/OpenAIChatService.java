package org.rag4j.integration.openai;

import com.openai.client.OpenAIClient;
import com.openai.core.JsonString;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;
import org.slf4j.Logger;

import java.util.List;

import static org.rag4j.integration.openai.OpenAIConstants.DEFAULT_MODEL;

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
        ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                .model(this.model)
                .temperature(1.0)
                .addDeveloperMessage(prompt.createSystemMessage())
                .addUserMessage(prompt.createUserMessage());

        ChatCompletion chatCompletion = this.client.chat().completions().create(createParamsBuilder.build());
        List<ChatCompletionMessage> messages =
                chatCompletion.choices().stream()
                        .map(ChatCompletion.Choice::message)
                        .toList();

        List<String> output = messages.stream()
                .flatMap(message -> message.content().stream())
                .toList();
        // Check if the message is an action
        if (output.isEmpty()) {
            throw new IllegalStateException("No output received from OpenAI API.");
        }
        if (output.size() > 1) {
            LOGGER.warn("Multiple messages received from OpenAI API, using the first one.");
        }

        String output_message = output.getFirst();
        LOGGER.info("Output message: {}", output_message);
        return output_message;
    }

    @Override
    public String askForJsonResponse(ChatPrompt prompt) {
        // TODO provide the schema to use for the json to return
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
