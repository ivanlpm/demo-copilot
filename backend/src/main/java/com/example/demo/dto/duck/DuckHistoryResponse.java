package com.example.demo.dto.duck;

import java.time.LocalDateTime;

/**
 * DTO for duck history records.
 */
public record DuckHistoryResponse(
        Long id,
        String url,
        String message,
        LocalDateTime createdAt
) {
}
