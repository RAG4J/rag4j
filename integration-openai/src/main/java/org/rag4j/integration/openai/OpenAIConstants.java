package org.rag4j.integration.openai;

/**
 * Constants for OpenAI engines. Contains constants for used models.
 */
public interface OpenAIConstants {
    String GPT5 = "gpt-5";
    String GPT5MINI = "gpt-5-mini";
    String GPT41 = "gpt-4.1";
    String GPT41MINI = "gpt-4.1-mini";
    String GPT4O = "gpt-4o";
    String GPT4 = "gpt-4";

    String DEFAULT_MODEL = GPT4O;

    String ADA2 = "text-embedding-ada-002";
    String SMALL = "text-embedding-3-small";

    String DEFAULT_EMBEDDING = SMALL;
}
