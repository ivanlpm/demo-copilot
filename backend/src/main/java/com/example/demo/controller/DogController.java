package com.example.demo.controller;

import com.example.demo.dto.dog.DogResponse;
import com.example.demo.service.DogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to provide endpoints for fetching dog data.
 */
@RestController
@RequestMapping("/api/dog")
@CrossOrigin(origins = "http://localhost:3000")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    /**
     * Endpoint to get a random dog image and its breed.
     * @return DogResponse with dog image URL and breed details.
     */
    @GetMapping
    public DogResponse getDog() {
        return dogService.getRandomDog();
    }
}
