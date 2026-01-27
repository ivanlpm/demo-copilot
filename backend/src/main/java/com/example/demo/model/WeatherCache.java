package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity to store cached weather data with expiration.
 */
@Entity
@Table(name = "weather_cache")
@Getter
@Setter
@NoArgsConstructor
public class WeatherCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String city;

    @Column(nullable = false, length = 5000)
    private String weatherData;

    @Column(nullable = false)
    private LocalDateTime cachedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public WeatherCache(String city, String weatherData, int cacheMinutes) {
        this.city = city.toLowerCase();
        this.weatherData = weatherData;
        this.cachedAt = LocalDateTime.now();
        this.expiresAt = this.cachedAt.plusMinutes(cacheMinutes);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
