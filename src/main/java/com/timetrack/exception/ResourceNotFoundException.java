package com.timetrack.exception;

/**
 * Exception thrown when a requested resource (user, log, setting) cannot be found.
 */
public class ResourceNotFoundException extends TimeTrackException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
