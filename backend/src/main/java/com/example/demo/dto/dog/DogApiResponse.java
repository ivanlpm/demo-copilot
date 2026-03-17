package com.example.demo.dto.dog;

/**
 * Data Transfer Object representing the raw response from the Dog CEO API.
 * The "message" field contains the image URL, and "status" indicates success/failure.
 */
public record DogApiResponse(String message, String status) {
}
