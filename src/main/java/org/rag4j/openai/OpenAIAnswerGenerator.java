package org.rag4j.openai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.*;
import org.rag4j.generation.AnswerGenerator;
import org.rag4j.util.KeyLoader;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI specific implementation of {@link AnswerGenerator}. The  task for this component is to generate an answer to a
 * provided question given a context. The answer is generated using the OpenAI API.
 */
public class OpenAIAnswerGenerator implements AnswerGenerator {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OpenAIAnswerGenerator.class);
    private final OpenAIClient client;

    private final String model;

    public OpenAIAnswerGenerator() {
        this(new KeyLoader(), OpenAIConstants.DEFAULT_MODEL);
    }

    public OpenAIAnswerGenerator(KeyLoader keyLoader, String model) {
        this.client = OpenAIFactory.obtainsClient(keyLoader.getOpenAIKey());
        this.model = model;
    }

    @Override
    public String generateAnswer(String question, String context) {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(
                "You are the tour guide for the Vasa Museum. You task is to answer question about the Vasa " +
                        "ship. Limit your answer to the context as provided. Do not use your own knowledge. The " +
                        "question is provided after 'question:'. The context after 'context:'. "));
        String contextMessage = String.format("Context: %s\nQuestion: %s\nAnswer:", question, context);
        chatMessages.add(new ChatRequestUserMessage(contextMessage));

        ChatCompletions chatCompletions = client.getChatCompletions(this.model, new ChatCompletionsOptions(chatMessages));

        CompletionsUsage usage = chatCompletions.getUsage();
        LOGGER.info("Usage: number of prompt token is {}, "
                        + "number of completion token is {}, and number of total tokens in request and response is {}.",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
        return chatCompletions.getChoices().getFirst().getMessage().getContent();
    }
}
