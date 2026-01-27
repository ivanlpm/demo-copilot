package com.example.demo.service;

import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.model.Duck;
import com.example.demo.repository.DuckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service to interact with the external Random Duck API and manage duck persistence.
 */
@Service
public class DuckService {

    private final RestClient restClient;
    private final DuckRepository duckRepository;

    public DuckService(RestClient.Builder restClientBuilder, 
                      DuckRepository duckRepository,
                      @Value("${duck.api.url}") String apiUrl) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.duckRepository = duckRepository;
    }

    /**
     * Fetches a random duck information from the external API and saves it to the database.
     * @return DuckResponse containing the image URL and a message.
     */
    public DuckResponse getRandomDuck() {
        DuckResponse response = this.restClient.get()
                .uri("/random")
                .retrieve()
                .body(DuckResponse.class);

        if (response != null) {
            // Save the duck to our database
            Duck duck = new Duck(response.url(), response.message());
            duckRepository.save(duck);
        }

        return response;
    }
}
