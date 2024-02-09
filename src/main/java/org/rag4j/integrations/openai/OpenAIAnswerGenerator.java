package org.rag4j.integrations.openai;

import org.rag4j.rag.generation.AnswerGenerator;
import org.rag4j.rag.generation.chat.ChatPrompt;
import org.rag4j.rag.generation.chat.ChatService;

import java.util.List;

/**
 * OpenAI specific implementation of {@link AnswerGenerator}. The  task for this component is to generate an answer to a
 * provided question given a context. The answer is generated using the OpenAI API.
 */
public class OpenAIAnswerGenerator implements AnswerGenerator {
    private final ChatService chatService;

    public OpenAIAnswerGenerator(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public String generateAnswer(String question, String context) {
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
