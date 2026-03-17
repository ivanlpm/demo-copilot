package com.example.demo.service;

import com.example.demo.dto.duck.DuckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class DuckServiceTest {

    @Mock
    private DuckPersistenceService duckPersistenceService;

    private DuckService duckService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        duckService = new DuckService(builder, duckPersistenceService, "https://api.test.com");
    }

    @Test
    @DisplayName("getRandomDuck should fetch from API and trigger async save")
    void getRandomDuck_Success_TriggersAsyncSave() {
        // given
        String jsonResponse = "{\"url\": \"https://duck.com/img.jpg\", \"message\": \"Quack!\"}";
        mockServer.expect(requestTo("https://api.test.com/random"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        DuckResponse response = duckService.getRandomDuck();

        // then
        assertThat(response).isNotNull();
        assertThat(response.url()).isEqualTo("https://duck.com/img.jpg");
        assertThat(response.message()).isEqualTo("Quack!");

        verify(duckPersistenceService, times(1)).saveDuckAsync(any());
        mockServer.verify();
    }
}
