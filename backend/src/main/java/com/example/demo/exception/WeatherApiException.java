package com.example.demo.exception;

/**
 * Exception thrown when weather API calls fail.
 */
public class WeatherApiException extends RuntimeException {
    public WeatherApiException(String message) {
        super(message);
    }

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
