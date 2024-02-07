package org.rag4j.quality;

import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OpenAIPromptTest {

    @Test
    public void constructPromptWithValidParamsShouldReturnChatCompletionsOptions() {
        OpenAIPrompt openAIPrompt = new OpenAIPrompt("System message template %s", "User message template %s");
        List<Object> systemParams = List.of("system");
        List<Object> userParams = List.of("user");

        ChatCompletionsOptions result = openAIPrompt.constructPrompt(systemParams, userParams);
        assertInstanceOf(ChatRequestSystemMessage.class, result.getMessages().get(0));
        assertEquals("System message template system", ((ChatRequestSystemMessage)result.getMessages().get(0)).getContent());
        assertInstanceOf(ChatRequestUserMessage.class, result.getMessages().get(1));
        // For some reason the getContent method is private, so we can't test it
    }

    @Test
    public void constructPromptWithMissingParamsShouldThrowException() {
        OpenAIPrompt openAIPrompt = new OpenAIPrompt("System message template %s %s", "User message template %s %s");
        List<Object> systemParams = List.of("system");
        List<Object> userParams = List.of("user");

        assertThrows(IllegalArgumentException.class, () -> openAIPrompt.constructPrompt(systemParams, userParams));
    }

    @Test
    public void readFromFileWithValidPathShouldReturnFileContent() {
        Path message = Path.of("/valid_path.txt");
        OpenAIPrompt openAIPrompt = new OpenAIPrompt(message, message);

        List<Object> systemParams = List.of("system");
        List<Object> userParams = List.of("user");

        ChatCompletionsOptions result = openAIPrompt.constructPrompt(systemParams, userParams);
        assertInstanceOf(ChatRequestSystemMessage.class, result.getMessages().get(0));
        assertEquals("Expected file content system", ((ChatRequestSystemMessage)result.getMessages().get(0)).getContent());
        assertInstanceOf(ChatRequestUserMessage.class, result.getMessages().get(1));
    }

    @Test
    public void readFromFileWithInvalidPathShouldThrowException() {
        Path message = Path.of("/invalid_path.txt");
        assertThrows(IllegalArgumentException.class, () -> new OpenAIPrompt(message, message));
    }
}