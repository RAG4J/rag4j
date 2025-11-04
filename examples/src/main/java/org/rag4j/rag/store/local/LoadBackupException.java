package org.rag4j.rag.store.local;

/**
 * Exception thrown when loading a backup fails.
 */
public class LoadBackupException extends RuntimeException {
    /**
     * Constructor.
     *
     * @param message The message.
     */
    public LoadBackupException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public LoadBackupException(String message, Throwable cause) {
        super(message, cause);
    }
}
