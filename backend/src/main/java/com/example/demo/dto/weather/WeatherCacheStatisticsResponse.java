package com.example.demo.dto.weather;

/**
 * DTO for weather cache statistics.
 */
public record WeatherCacheStatisticsResponse(
        long totalCached,
        long activeCaches,
        long expiredCaches
) {
}
