package com.example.demo.service;

import com.example.demo.model.Duck;
import com.example.demo.repository.DuckRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for persisting duck data asynchronously,
 * allowing the main request thread to return immediately.
 */
@Service
public class DuckPersistenceService {

    private final DuckRepository duckRepository;

    public DuckPersistenceService(DuckRepository duckRepository) {
        this.duckRepository = duckRepository;
    }

    /**
     * Persists the given duck to the database asynchronously on the duck task executor thread pool.
     *
     * @param duck the duck entity to save
     */
    @Async("duckTaskExecutor")
    @Transactional
    public void saveDuckAsync(Duck duck) {
        duckRepository.save(duck);
    }
}
