package com.example.demo.service;

import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.model.Duck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service to interact with the external Random Duck API and manage duck persistence.
 */
@Service
public class DuckService {

    private final RestClient restClient;
    private final DuckPersistenceService duckPersistenceService;

    public DuckService(RestClient.Builder restClientBuilder,
                       DuckPersistenceService duckPersistenceService,
                       @Value("${duck.api.url}") String apiUrl) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.duckPersistenceService = duckPersistenceService;
    }

    /**
     * Fetches a random duck from the external API and asynchronously persists it to the database.
     * The response is returned to the caller immediately without waiting for the save to complete.
     *
     * @return DuckResponse containing the image URL and a message.
     */
    public DuckResponse getRandomDuck() {
        DuckResponse response = this.restClient.get()
                .uri("/random")
                .retrieve()
                .body(DuckResponse.class);

        if (response != null) {
            Duck duck = new Duck(response.url(), response.message());
            duckPersistenceService.saveDuckAsync(duck);
        }

        return response;
    }
}
