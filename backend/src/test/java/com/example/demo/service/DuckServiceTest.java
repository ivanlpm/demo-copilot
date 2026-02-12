package com.example.demo.service;

import com.example.demo.dto.duck.DuckHistoryResponse;
import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.dto.duck.DuckStatisticsResponse;
import com.example.demo.model.Duck;
import com.example.demo.repository.DuckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class DuckServiceTest {

    @Mock
    private DuckRepository duckRepository;

    private DuckService duckService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        duckService = new DuckService(builder, duckRepository, "https://api.test.com");
    }

    @Test
    @DisplayName("getRandomDuck should fetch from API and save to repository")
    void getRandomDuck_Success_SavesToRepo() {
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
        
        verify(duckRepository, times(1)).save(any());
        mockServer.verify();
    }

    @Test
    @DisplayName("getDuckHistory should return paginated duck history")
    void getDuckHistory_ReturnsPaginatedHistory() {
        // given
        Duck duck1 = new Duck("url1", "msg1");
        Duck duck2 = new Duck("url2", "msg2");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Duck> duckPage = new PageImpl<>(List.of(duck1, duck2));
        
        when(duckRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(duckPage);

        // when
        Page<DuckHistoryResponse> result = duckService.getDuckHistory(pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).url()).isEqualTo("url1");
        verify(duckRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("getDuckById should return duck when found")
    void getDuckById_WhenFound_ReturnsDuck() {
        // given
        Duck duck = new Duck("url", "message");
        when(duckRepository.findById(1L)).thenReturn(Optional.of(duck));

        // when
        Optional<DuckHistoryResponse> result = duckService.getDuckById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().url()).isEqualTo("url");
        verify(duckRepository).findById(1L);
    }

    @Test
    @DisplayName("getDuckById should return empty when not found")
    void getDuckById_WhenNotFound_ReturnsEmpty() {
        // given
        when(duckRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<DuckHistoryResponse> result = duckService.getDuckById(999L);

        // then
        assertThat(result).isEmpty();
        verify(duckRepository).findById(999L);
    }

    @Test
    @DisplayName("deleteDuck should return true when duck exists")
    void deleteDuck_WhenExists_ReturnsTrue() {
        // given
        when(duckRepository.existsById(1L)).thenReturn(true);

        // when
        boolean result = duckService.deleteDuck(1L);

        // then
        assertThat(result).isTrue();
        verify(duckRepository).existsById(1L);
        verify(duckRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteDuck should return false when duck does not exist")
    void deleteDuck_WhenNotExists_ReturnsFalse() {
        // given
        when(duckRepository.existsById(999L)).thenReturn(false);

        // when
        boolean result = duckService.deleteDuck(999L);

        // then
        assertThat(result).isFalse();
        verify(duckRepository).existsById(999L);
        verify(duckRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getDuckStatistics should return statistics")
    void getDuckStatistics_ReturnsStatistics() {
        // given
        Duck oldestDuck = new Duck("url1", "msg1");
        Duck newestDuck = new Duck("url2", "msg2");
        LocalDateTime oldDate = LocalDateTime.now().minusDays(10);
        LocalDateTime newDate = LocalDateTime.now();
        
        when(duckRepository.count()).thenReturn(100L);
        when(duckRepository.countByCreatedAtAfter(any())).thenReturn(5L);
        when(duckRepository.findFirstByOrderByCreatedAtAsc()).thenReturn(Optional.of(oldestDuck));
        when(duckRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(newestDuck));

        // when
        DuckStatisticsResponse result = duckService.getDuckStatistics();

        // then
        assertThat(result).isNotNull();
        assertThat(result.totalFetched()).isEqualTo(100L);
        assertThat(result.fetchedToday()).isEqualTo(5L);
        verify(duckRepository).count();
        verify(duckRepository).countByCreatedAtAfter(any());
    }
}
