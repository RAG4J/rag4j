package org.rag4j.rag.generation.chat;

/**
 * Service used to interact with a chat, generally based on a Large Language Model. The {@link ChatPrompt} is used to
 * provide the context and the initial message to the chat, and the service will return the response from the chat as
 * a string.
 */
public interface ChatService {
    /**
     * Asks the chat for a response based on the provided prompt.
     * @param prompt the prompt to use to ask the chat for a response
     * @return the response from the chat
     */
    String askForResponse(ChatPrompt prompt);

    String askForJsonResponse(ChatPrompt prompt);
}
