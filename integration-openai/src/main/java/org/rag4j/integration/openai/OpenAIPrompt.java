package org.rag4j.integration.openai;

import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.rag4j.rag.generation.chat.ChatPrompt;

/**
 * TODO: We might want to remove this class and directly use the method in OpenAIChatService.
 */
public class OpenAIPrompt {

    public static ChatCompletionCreateParams.Builder constructPrompt(ChatPrompt prompt, Double temperature, String model) {
        return ChatCompletionCreateParams.builder()
                .model(model)
                .addDeveloperMessage(prompt.createSystemMessage())
                .addUserMessage(prompt.createUserMessage());
    }


}
