package com.example.demo.service;

import com.example.demo.dto.duck.DuckHistoryResponse;
import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.dto.duck.DuckStatisticsResponse;
import com.example.demo.model.Duck;
import com.example.demo.repository.DuckRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

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
    @Transactional
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

    /**
     * Get paginated list of duck history.
     */
    @Transactional(readOnly = true)
    public Page<DuckHistoryResponse> getDuckHistory(Pageable pageable) {
        return duckRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(duck -> new DuckHistoryResponse(
                        duck.getId(),
                        duck.getUrl(),
                        duck.getMessage(),
                        duck.getCreatedAt()
                ));
    }

    /**
     * Get a specific duck by ID.
     */
    @Transactional(readOnly = true)
    public Optional<DuckHistoryResponse> getDuckById(Long id) {
        return duckRepository.findById(id)
                .map(duck -> new DuckHistoryResponse(
                        duck.getId(),
                        duck.getUrl(),
                        duck.getMessage(),
                        duck.getCreatedAt()
                ));
    }

    /**
     * Delete a duck by ID.
     */
    @Transactional
    public boolean deleteDuck(Long id) {
        if (duckRepository.existsById(id)) {
            duckRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get duck statistics.
     */
    @Transactional(readOnly = true)
    public DuckStatisticsResponse getDuckStatistics() {
        long totalFetched = duckRepository.count();
        
        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long fetchedToday = duckRepository.countByCreatedAtAfter(startOfToday);
        
        LocalDateTime oldestDuck = duckRepository.findFirstByOrderByCreatedAtAsc()
                .map(Duck::getCreatedAt)
                .orElse(null);
        
        LocalDateTime newestDuck = duckRepository.findFirstByOrderByCreatedAtDesc()
                .map(Duck::getCreatedAt)
                .orElse(null);
        
        return new DuckStatisticsResponse(
                totalFetched,
                fetchedToday,
                oldestDuck,
                newestDuck
        );
    }
}
