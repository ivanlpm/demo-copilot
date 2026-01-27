package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Weather API.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {
    private Api api = new Api();
    private int cacheMinutes = 30;
    private RateLimit ratelimit = new RateLimit();

    @Data
    public static class Api {
        private String url;
        private String key = "demo";
    }

    @Data
    public static class RateLimit {
        private int max = 60;
        private int window = 60;
    }
}
