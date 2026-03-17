package com.example.demo.service;

import com.example.demo.model.Duck;
import com.example.demo.repository.DuckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DuckPersistenceServiceTest {

    @Mock
    private DuckRepository duckRepository;

    private DuckPersistenceService duckPersistenceService;

    @BeforeEach
    void setUp() {
        duckPersistenceService = new DuckPersistenceService(duckRepository);
    }

    @Test
    @DisplayName("saveDuckAsync should delegate save to the repository")
    // Note: @Async is a Spring AOP concern and is not active in this unit test context.
    // This test verifies the delegation to the repository; the async behavior is
    // covered by the Spring context wiring in integration tests.
    void saveDuckAsync_ValidDuck_SavesToRepository() {
        // given
        Duck duck = new Duck("https://duck.com/test.jpg", "Quack!");

        // when
        duckPersistenceService.saveDuckAsync(duck);

        // then
        verify(duckRepository, times(1)).save(duck);
    }
}
