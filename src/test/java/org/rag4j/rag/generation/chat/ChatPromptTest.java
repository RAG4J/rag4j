package org.rag4j.rag.generation.chat;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChatPromptTest {
    @Test
    public void constructPromptWithValidParams() {
        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessage("System message template %s")
                .userMessage("User message template %s")
                .systemParams(List.of("system"))
                .userParams(List.of("user"))
                .build();

        assertEquals("System message template system", prompt.createSystemMessage());
        assertEquals("User message template user", prompt.createUserMessage());
    }

    @Test
    public void constructPromptWithValidParamsOnlyUser() {
        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessage("System message template")
                .userMessage("User message template %s")
                .userParams(List.of("user"))
                .build();

        assertEquals("System message template", prompt.createSystemMessage());
        assertEquals("User message template user", prompt.createUserMessage());
    }

    @Test
    public void constructPromptWithMissingParamsShouldThrowException() {
        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessage("System message template %s %s")
                .userMessage("User message template %s %s")
                .userParams(List.of("user"))
                .systemParams(List.of("system"))
                .build();

        assertThrows(IllegalArgumentException.class, prompt::createSystemMessage);
    }

    @Test
    public void readFromFileWithValidPathShouldReturnFileContent() {
        ChatPrompt prompt = ChatPrompt.builder()
                .systemMessageFilename("/valid_path.txt")
                .userMessageFilename("/valid_path.txt")
                .systemParams(List.of("system"))
                .userParams(List.of("user"))
                .build();

        assertEquals("Expected file content system", prompt.createSystemMessage());
        assertEquals("Expected file content user", prompt.createUserMessage());
    }

    @Test
    public void readFromFileWithInvalidPathShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ChatPrompt.builder()
                    .systemMessageFilename("/invalid_path.txt")
                    .userMessageFilename("/invalid_path.txt")
                    .build();
        });
    }

}
