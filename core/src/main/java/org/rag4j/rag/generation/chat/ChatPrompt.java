package org.rag4j.rag.generation.chat;

import lombok.Builder;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a chat prompt with a system message and a user message. The messages can contain parameters that will be
 * replaced by the values in the params list. You can provide the messages as strings or as filenames, in which case the
 * file will be read and the content will be used as the message.
 */
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

    /**
     * Creates a system message using the system message template and the system params.
     * @return the system message
     */
    public String createSystemMessage() {
        return createMessage(systemMessage, systemParams);
    }

    /**
     * Creates a user message using the user message template and the user params.
     * @return the user message
     */
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

    /**
     * Special class to override the Lombok builder methods to allow reading the messages from files.
     */
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
