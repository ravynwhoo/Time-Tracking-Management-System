package com.timetrack.exception;

/**
 * Exception thrown when validation constraints fail.
 */
public class ValidationException extends TimeTrackException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
