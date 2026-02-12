package com.example.demo.dto.weather;

import java.time.LocalDateTime;

/**
 * DTO for weather cache entry information.
 */
public record WeatherCacheEntryResponse(
        String city,
        LocalDateTime cachedAt,
        LocalDateTime expiresAt,
        boolean expired
) {
}
