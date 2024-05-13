package org.rag4j.rag.generation;

import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;

import java.util.List;

/**
 * Component that generates a question given a text.
 */
public class QuestionGenerator {
    private final ChatService chatService;

    public QuestionGenerator(ChatService chatService) {
        this.chatService = chatService;
    }

    public String generateQuestion(String text) {
        ChatPrompt chatPrompt = ChatPrompt.builder()
                .systemMessage("Read the provided text carefully. Extract facts from the text. Use the facts to " +
                        "generate a question. Requirements for the question are: " +
                        "\n- The question must be a question that can be answered by reading the text. " +
                        "\n- Prevent using names or exact dates in the question. " +
                        "\n- The question must be one sentence only. " +
                        "\n- The question must end with a '?' character. " +
                        "\n- Use different words than the ones in the text to form the question. " +
                        "\n\nThe result should only contain the generated question, nothing else.")
                .userMessage("provided text: %s%nGenerated question:")
                .userParams(List.of(text))
                .build();

        return chatService.askForResponse(chatPrompt);
    }
}
