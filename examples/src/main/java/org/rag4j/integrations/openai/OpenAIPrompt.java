package org.rag4j.integrations.openai;

import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import org.rag4j.rag.generation.chat.ChatPrompt;

import java.util.ArrayList;
import java.util.List;

public class OpenAIPrompt {

    public static ChatCompletionsOptions constructPrompt(ChatPrompt prompt, Double temperature) {
        List<ChatRequestMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatRequestSystemMessage(prompt.createSystemMessage()));
        chatMessages.add(new ChatRequestUserMessage(prompt.createUserMessage()));
        ChatCompletionsOptions options = new ChatCompletionsOptions(chatMessages);
        options.setTemperature(temperature);
        return options;
    }


}
