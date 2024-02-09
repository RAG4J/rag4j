package org.rag4j.integrations.openai;

import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import org.junit.jupiter.api.Test;
import org.rag4j.rag.generation.chat.ChatPrompt;

import static org.junit.jupiter.api.Assertions.*;

public class OpenAIPromptTest {

@Test
public void constructPromptWithValidParamsShouldReturnChatCompletionsOptionsWithCorrectTemperature() {
    ChatPrompt chatPrompt = ChatPrompt.builder()
            .systemMessage("System message")
            .userMessage("User message")
            .build();

    Double temperature = 0.5;

    ChatCompletionsOptions result = OpenAIPrompt.constructPrompt(chatPrompt, temperature);

    assertInstanceOf(ChatRequestSystemMessage.class, result.getMessages().get(0));
    assertEquals("System message", ((ChatRequestSystemMessage)result.getMessages().get(0)).getContent());
    assertInstanceOf(ChatRequestUserMessage.class, result.getMessages().get(1));
    assertEquals(temperature, result.getTemperature());
}

@Test
public void constructPromptWithNullPromptShouldThrowException() {
    assertThrows(NullPointerException.class, () -> OpenAIPrompt.constructPrompt(null, 0.5));
}

@Test
public void constructPromptWithNullTemperatureShouldReturnChatCompletionsOptionsWithDefaultTemperature() {
    ChatPrompt chatPrompt = ChatPrompt.builder()
            .systemMessage("System message")
            .userMessage("User message")
            .build();

    ChatCompletionsOptions result = OpenAIPrompt.constructPrompt(chatPrompt, null);

    assertInstanceOf(ChatRequestSystemMessage.class, result.getMessages().get(0));
    assertEquals("System message", ((ChatRequestSystemMessage)result.getMessages().get(0)).getContent());
    assertInstanceOf(ChatRequestUserMessage.class, result.getMessages().get(1));
    assertNull(result.getTemperature());
}}