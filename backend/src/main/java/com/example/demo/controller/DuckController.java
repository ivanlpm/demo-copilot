package com.example.demo.controller;

import com.example.demo.dto.duck.DuckHistoryResponse;
import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.dto.duck.DuckStatisticsResponse;
import com.example.demo.service.DuckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to provide endpoints for fetching duck data.
 */
@RestController
@RequestMapping("/api/duck")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend access
public class DuckController {

    private final DuckService duckService;

    public DuckController(DuckService duckService) {
        this.duckService = duckService;
    }

    /**
     * Endpoint to get a random duck.
     * @return DuckResponse with duck image details.
     */
    @GetMapping
    public DuckResponse getDuck() {
        return duckService.getRandomDuck();
    }

    /**
     * Get paginated duck history.
     */
    @GetMapping("/history")
    public ResponseEntity<Page<DuckHistoryResponse>> getDuckHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(duckService.getDuckHistory(pageable));
    }

    /**
     * Get a specific duck by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DuckHistoryResponse> getDuckById(@PathVariable Long id) {
        return duckService.getDuckById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a duck by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDuck(@PathVariable Long id) {
        boolean deleted = duckService.deleteDuck(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Get duck statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<DuckStatisticsResponse> getDuckStatistics() {
        return ResponseEntity.ok(duckService.getDuckStatistics());
    }
}
