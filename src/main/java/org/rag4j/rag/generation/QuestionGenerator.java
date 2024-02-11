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
                .systemMessage("You are a content writer reading a text and writing questions that are answered in that text. " +
                        "Use the context as provided and nothing else to come up with one question. The question should " +
                        "be a question that a person that does not know a lot about the context could ask. Do not use " +
                        "names in your question or exact dates. Do not use the exact words in the context. Each question " +
                        "must be one sentence only end always end with a '?' character. The context is provided after " +
                        "'context:'. The result should only contain the generated question, nothing else.")
                .userMessage("Context: %s%nGenerated question:")
                .userParams(List.of(text))
                .build();

        return chatService.askForResponse(chatPrompt);
    }
}
