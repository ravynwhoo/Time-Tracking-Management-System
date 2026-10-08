package com.timetrack.exception;

/**
 * Exception thrown when authentication or authorization fails.
 */
public class AuthenticationException extends TimeTrackException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }
}
