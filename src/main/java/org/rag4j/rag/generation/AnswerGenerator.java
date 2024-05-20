package org.rag4j.rag.generation;

import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;

import java.util.List;

/**
 * Component that generates an answer to a question given a context.
 */
public class AnswerGenerator {
    private final ChatService chatService;

    public AnswerGenerator(ChatService chatService) {
        this.chatService = chatService;
    }

    public String generateAnswer(String question, String context){
        ChatPrompt chatPrompt = ChatPrompt.builder().systemMessage(
                        "You are an assistant answering questions using the context provided. If the context does " +
                                "not contain the answer, you should tell you cannot answer using the context. " +
                                "The question is provided after 'question:'. The context after 'context:'. "
                )
                .userMessage("Context: %s\nQuestion: %s\nAnswer:")
                .userParams(List.of(context, question))
                .build();

        return chatService.askForResponse(chatPrompt);

    }
}
