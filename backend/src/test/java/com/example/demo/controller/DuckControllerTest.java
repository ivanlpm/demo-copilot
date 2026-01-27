package com.example.demo.controller;

import com.example.demo.dto.duck.DuckResponse;
import com.example.demo.service.DuckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
