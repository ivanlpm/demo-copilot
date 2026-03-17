package com.example.demo.dto.dog;

/**
 * Data Transfer Object representing the dog data returned to the client.
 */
public record DogResponse(String url, String breed) {
}
