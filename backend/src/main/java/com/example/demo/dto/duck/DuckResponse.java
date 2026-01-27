package com.example.demo.dto.duck;

/**
 * Data Transfer Object representing the response from the Random Duck API.
 */
public record DuckResponse(String url, String message) {
}
