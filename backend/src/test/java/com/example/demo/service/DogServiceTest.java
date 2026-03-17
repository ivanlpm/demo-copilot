package com.example.demo.service;

import com.example.demo.dto.dog.DogResponse;
import com.example.demo.repository.DogRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    private DogService dogService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        dogService = new DogService(builder, dogRepository, "https://api.test.com");
    }

    @Test
    @DisplayName("getRandomDog should fetch from API, extract breed, and save to repository")
    void getRandomDog_Success_SavesToRepo() {
        // given
        String jsonResponse = "{\"message\": \"https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg\", \"status\": \"success\"}";
        mockServer.expect(requestTo("https://api.test.com/breeds/image/random"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        DogResponse response = dogService.getRandomDog();

        // then
        assertThat(response).isNotNull();
        assertThat(response.url()).isEqualTo("https://images.dog.ceo/breeds/hound-afghan/n02088094_1003.jpg");
        assertThat(response.breed()).isEqualTo("hound-afghan");

        verify(dogRepository, times(1)).save(any());
        mockServer.verify();
    }

    @Test
    @DisplayName("getRandomDog should return 'unknown' breed when URL has no recognisable breed path")
    void getRandomDog_UnrecognisedUrl_ReturnsUnknownBreed() {
        // given
        String jsonResponse = "{\"message\": \"https://other-host.com/image.jpg\", \"status\": \"success\"}";
        mockServer.expect(requestTo("https://api.test.com/breeds/image/random"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        DogResponse response = dogService.getRandomDog();

        // then
        assertThat(response).isNotNull();
        assertThat(response.breed()).isEqualTo("unknown");
        verify(dogRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("getRandomDog should return null and not save when API response message is null")
    void getRandomDog_NullMessageInApiResponse_ReturnsNull() {
        // given
        String jsonResponse = "{\"message\": null, \"status\": \"error\"}";
        mockServer.expect(requestTo("https://api.test.com/breeds/image/random"))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // when
        DogResponse response = dogService.getRandomDog();

        // then
        assertThat(response).isNull();
        verify(dogRepository, never()).save(any());
    }
}
