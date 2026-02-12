package com.example.demo.dto.duck;

import java.time.LocalDateTime;

/**
 * DTO for duck statistics.
 */
public record DuckStatisticsResponse(
        long totalFetched,
        long fetchedToday,
        LocalDateTime oldestDuck,
        LocalDateTime newestDuck
) {
}
