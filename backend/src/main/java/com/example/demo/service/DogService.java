package com.example.demo.service;

import com.example.demo.dto.dog.DogApiResponse;
import com.example.demo.dto.dog.DogResponse;
import com.example.demo.model.Dog;
import com.example.demo.repository.DogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service to interact with the external Dog CEO API and manage dog persistence.
 */
@Service
public class DogService {

    private final RestClient restClient;
    private final DogRepository dogRepository;

    public DogService(RestClient.Builder restClientBuilder,
                      DogRepository dogRepository,
                      @Value("${dog.api.url}") String apiUrl) {
        this.restClient = restClientBuilder.baseUrl(apiUrl).build();
        this.dogRepository = dogRepository;
    }

    /**
     * Fetches a random dog image from the Dog CEO API and saves it to the database.
     * @return DogResponse containing the image URL and the dog's breed.
     */
    public DogResponse getRandomDog() {
        DogApiResponse apiResponse = this.restClient.get()
                .uri("/breeds/image/random")
                .retrieve()
                .body(DogApiResponse.class);

        if (apiResponse == null || apiResponse.message() == null) {
            return null;
        }

        String imageUrl = apiResponse.message();
        String breed = extractBreed(imageUrl);

        Dog dog = new Dog(imageUrl, breed);
        dogRepository.save(dog);

        return new DogResponse(imageUrl, breed);
    }

    /**
     * Extracts the breed name from the Dog CEO image URL.
     * URL format: https://images.dog.ceo/breeds/{breed}/{filename}
     * @param url the image URL from the Dog CEO API
     * @return the breed name, or "unknown" if it cannot be determined
     */
    private String extractBreed(String url) {
        if (url == null || url.isBlank()) {
            return "unknown";
        }
        String[] parts = url.split("/");
        // URL path: .../breeds/{breed}/{filename}
        for (int i = 0; i < parts.length - 1; i++) {
            if ("breeds".equals(parts[i]) && i + 1 < parts.length) {
                return parts[i + 1];
            }
        }
        return "unknown";
    }
}
