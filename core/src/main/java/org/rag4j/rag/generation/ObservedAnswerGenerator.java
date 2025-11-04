package org.rag4j.rag.generation;

import org.rag4j.rag.generation.chat.ChatService;
import org.rag4j.rag.tracker.RAGTracker;

/**
 * Decorator for an {@link AnswerGenerator} that tracks the generated answer, question and context. To track the answer
 * the {@link RAGTracker} is used to store the answer, question and context in a thread local variable.
 */
public class ObservedAnswerGenerator extends AnswerGenerator {

    public ObservedAnswerGenerator(ChatService chatService) {
        super(chatService);
    }

    @Override
    public String generateAnswer(String question, String context) {
        String answer = super.generateAnswer(question, context);
        RAGTracker.setAnswer(answer);
        RAGTracker.setQuestion(question);
        RAGTracker.setContext(context);
        return answer;
    }
}
