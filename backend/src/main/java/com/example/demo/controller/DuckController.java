package com.example.demo.controller;

import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.service.DuckService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
