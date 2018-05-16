package com.iab.gdpr.exception;

public class GdprException extends RuntimeException {
    public GdprException(String message, Throwable cause) {
        super(message, cause);
    }

    public GdprException(String message) {
        super(message);
    }
}
