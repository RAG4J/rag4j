package org.rag4j.rag.generation;

/**
 * Component that generates a question given a text.
 */
public interface QuestionGenerator {
    String generateQuestion(String text);
}
