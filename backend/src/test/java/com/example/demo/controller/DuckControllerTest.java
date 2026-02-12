package com.example.demo.controller;

import com.example.demo.dto.duck.DuckHistoryResponse;
import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.dto.duck.DuckStatisticsResponse;
import com.example.demo.service.DuckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DuckControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @MockitoBean
    private DuckService duckService;

    @Test
    @DisplayName("GET /api/duck should return duck data from service")
    void getDuck_ReturnsDuckData() throws Exception {
        // given
        DuckResponse mockResponse = new DuckResponse("https://duck.com/1.jpg", "Hello!");
        when(duckService.getRandomDuck()).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/duck"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://duck.com/1.jpg"))
                .andExpect(jsonPath("$.message").value("Hello!"));
    }

    @Test
    @DisplayName("GET /api/duck/history should return paginated duck history")
    void getDuckHistory_ReturnsPaginatedHistory() throws Exception {
        // given
        DuckHistoryResponse duck1 = new DuckHistoryResponse(1L, "url1", "msg1", LocalDateTime.now());
        DuckHistoryResponse duck2 = new DuckHistoryResponse(2L, "url2", "msg2", LocalDateTime.now());
        Page<DuckHistoryResponse> mockPage = new PageImpl<>(List.of(duck1, duck2), PageRequest.of(0, 10), 2);
        
        when(duckService.getDuckHistory(any())).thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/duck/history")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].url").value("url1"))
                .andExpect(jsonPath("$.content[1].url").value("url2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/duck/{id} should return duck when found")
    void getDuckById_WhenFound_ReturnsDuck() throws Exception {
        // given
        DuckHistoryResponse mockDuck = new DuckHistoryResponse(1L, "url", "message", LocalDateTime.now());
        when(duckService.getDuckById(1L)).thenReturn(Optional.of(mockDuck));

        // when & then
        mockMvc.perform(get("/api/duck/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value("url"))
                .andExpect(jsonPath("$.message").value("message"));
    }

    @Test
    @DisplayName("GET /api/duck/{id} should return 404 when not found")
    void getDuckById_WhenNotFound_Returns404() throws Exception {
        // given
        when(duckService.getDuckById(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/duck/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/duck/{id} should return 204 when duck deleted")
    void deleteDuck_WhenDeleted_Returns204() throws Exception {
        // given
        when(duckService.deleteDuck(1L)).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/api/duck/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/duck/{id} should return 404 when duck not found")
    void deleteDuck_WhenNotFound_Returns404() throws Exception {
        // given
        when(duckService.deleteDuck(999L)).thenReturn(false);

        // when & then
        mockMvc.perform(delete("/api/duck/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/duck/statistics should return statistics")
    void getDuckStatistics_ReturnsStatistics() throws Exception {
        // given
        DuckStatisticsResponse mockStats = new DuckStatisticsResponse(
                100L, 5L, 
                LocalDateTime.now().minusDays(10), 
                LocalDateTime.now()
        );
        when(duckService.getDuckStatistics()).thenReturn(mockStats);

        // when & then
        mockMvc.perform(get("/api/duck/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalFetched").value(100))
                .andExpect(jsonPath("$.fetchedToday").value(5));
    }
}
