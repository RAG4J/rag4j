package org.rag4j.generation;

import org.rag4j.tracker.RAGTracker;

/**
 * Decorator for an {@link AnswerGenerator} that tracks the generated answer, question and context. To track the answer
 * the {@link RAGTracker} is used to store the answer, question and context in a thread local variable.
 */
public class ObservedAnswerGenerator implements AnswerGenerator {
    private final AnswerGenerator generator;

    public ObservedAnswerGenerator(AnswerGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String generateAnswer(String question, String context) {
        String answer = generator.generateAnswer(question, context);
        RAGTracker.setAnswer(answer);
        RAGTracker.setQuestion(question);
        RAGTracker.setContext(context);
        return answer;
    }
}
