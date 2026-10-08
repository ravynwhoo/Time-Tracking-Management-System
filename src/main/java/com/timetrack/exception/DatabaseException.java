package com.timetrack.exception;

/**
 * Exception thrown when a database or persistence operation fails.
 */
public class DatabaseException extends TimeTrackException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
