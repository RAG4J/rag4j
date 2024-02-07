package org.rag4j.chat;

import lombok.Builder;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

@Builder
public class ChatPrompt {
    private String systemMessage;
    private String userMessage;
    private String systemMessageFilename;
    private String userMessageFilename;
    @Builder.Default
    private List<Object> systemParams = List.of();
    @Builder.Default
    private List<Object> userParams = List.of();

    public String createSystemMessage() {
        return createMessage(systemMessage, systemParams);
    }

    public String createUserMessage() {
        return createMessage(userMessage, userParams);
    }

    private static String readFromFile(String path) {
        InputStream inputStream = ChatPrompt.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + path);
        }
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\Z");
        String promptTemplate = scanner.next();
        scanner.close();

        return promptTemplate;
    }

    private String createMessage(String template, List<Object> params) {
        return String.format(template, params.toArray());
    }

    public static class ChatPromptBuilder {
        public ChatPrompt.ChatPromptBuilder systemMessageFilename(String systemMessageFilename) {
            this.systemMessage(readFromFile(systemMessageFilename));
            return this;
        }

        public ChatPrompt.ChatPromptBuilder userMessageFilename(String userMessageFilename) {
            this.userMessage(readFromFile(userMessageFilename));
            return this;
        }
    }

}
