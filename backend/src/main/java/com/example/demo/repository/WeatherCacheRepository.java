package com.example.demo.repository;

import com.example.demo.model.WeatherCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for WeatherCache entity.
 */
@Repository
public interface WeatherCacheRepository extends JpaRepository<WeatherCache, Long> {
    Optional<WeatherCache> findByCity(String city);
    void deleteByCity(String city);
}
