package org.rag4j.quality;

import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import lombok.Builder;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OpenAIPrompt {
    private static final double DEFAULT_TEMPERATURE = 1.0;
    private final String systemMessage;
    private final String userMessage;
    private final double temperature;

    public OpenAIPrompt(Path systemMessage, Path userMessage) {
        this(systemMessage, userMessage, DEFAULT_TEMPERATURE);
    }

    public OpenAIPrompt(Path systemMessage, Path userMessage, double temperature) {
        this(readFromFile(systemMessage.toString()),
                readFromFile(userMessage.toString()),
                temperature);
    }

    public OpenAIPrompt(String systemMessage, String userMessage) {
        this(systemMessage, userMessage, DEFAULT_TEMPERATURE);
    }

    public OpenAIPrompt(String systemMessage, String userMessage, double temperature) {
        this.systemMessage = systemMessage;
        this.userMessage = userMessage;
        this.temperature = temperature;
    }

    public ChatCompletionsOptions constructPrompt(List<Object> systemParams, List<Object> userParams) {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(createMessage(systemMessage, systemParams)));
        chatMessages.add(new ChatRequestUserMessage(createMessage(userMessage, userParams)));
        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
        options.setTemperature(this.temperature);
        return options;
    }

    private String createMessage(String template, List<Object> params) {
        return String.format(template, params.toArray());
    }

    private static String readFromFile(String path) {
        InputStream inputStream = OpenAIPrompt.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + path);
        }
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\Z");
        String promptTemplate = scanner.next();
        scanner.close();

        return promptTemplate;
    }
}
