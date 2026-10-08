package com.timetrack.exception;

/**
 * Root checked exception for the TimeTrack application.
 */
public class TimeTrackException extends Exception {
    private static final long serialVersionUID = 1L;

    public TimeTrackException(String message) {
        super(message);
    }

    public TimeTrackException(String message, Throwable cause) {
        super(message, cause);
    }
}
