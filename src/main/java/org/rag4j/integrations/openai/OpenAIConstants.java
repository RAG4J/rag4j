package org.rag4j.integrations.openai;

/**
 * Constants for OpenAI engines. Contains constants for used models.
 */
public interface OpenAIConstants {
    String GPT4O = "gpt-4o";
    String GPT4 = "gpt-4";
    String GPT4TURBO = "gpt-4-turbo";
    String GPT35 = "gpt-3.5-turbo";

    String DEFAULT_MODEL = GPT4O;

    String ADA2 = "text-embedding-ada-002";
    String SMALL = "text-embedding-3-small";

    String DEFAULT_EMBEDDING = SMALL;
}
