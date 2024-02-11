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
                        "You are the tour guide for the Vasa Museum. You task is to answer question about the Vasa " +
                                "ship. Limit your answer to the context as provided. Do not use your own knowledge. " +
                                "The question is provided after 'question:'. The context after 'context:'. "
                )
                .userMessage("Context: %s\nQuestion: %s\nAnswer:")
                .userParams(List.of(context, question))
                .build();

        return chatService.askForResponse(chatPrompt);

    }
}
