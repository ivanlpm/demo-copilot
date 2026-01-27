package com.example.demo.service;

import com.example.demo.exception.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple in-memory rate limiter to prevent excessive API calls.
 */
@Component
public class RateLimiter {

    private final ConcurrentMap<String, RequestTracker> trackers = new ConcurrentHashMap<>();

    /**
     * Check if request is allowed under rate limit.
     * @param key Identifier for rate limiting (e.g., city name)
     * @param maxRequests Maximum requests allowed
     * @param windowMinutes Time window in minutes
     */
    public void checkRateLimit(String key, int maxRequests, int windowMinutes) {
        trackers.compute(key, (k, tracker) -> {
            LocalDateTime now = LocalDateTime.now();
            
            if (tracker == null || now.isAfter(tracker.windowEnd)) {
                return new RequestTracker(1, now.plusMinutes(windowMinutes));
            }
            
            if (tracker.count >= maxRequests) {
                throw new RateLimitExceededException(
                    "Rate limit exceeded for " + key + ". Try again later."
                );
            }
            
            tracker.count++;
            return tracker;
        });
    }

    private static class RequestTracker {
        int count;
        LocalDateTime windowEnd;

        RequestTracker(int count, LocalDateTime windowEnd) {
            this.count = count;
            this.windowEnd = windowEnd;
        }
    }
}
