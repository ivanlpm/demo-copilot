package com.example.demo.exception;

/**
 * Exception thrown when API rate limit is exceeded.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
