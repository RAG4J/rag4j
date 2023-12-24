package org.rag4j.generation;

/**
 * Component that generates an answer to a question given a context.
 */
public interface AnswerGenerator {
    String generateAnswer(String question, String context);
}
