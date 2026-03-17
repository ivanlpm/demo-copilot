package com.example.demo.controller;

import com.example.demo.dto.dog.DogResponse;
import com.example.demo.service.DogService;
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
class DogControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @MockitoBean
    private DogService dogService;

    @Test
    @DisplayName("GET /api/dog should return dog data from service")
    void getDog_ReturnsDogData() throws Exception {
        // given
        DogResponse mockResponse = new DogResponse("https://images.dog.ceo/breeds/labrador/1.jpg", "labrador");
        when(dogService.getRandomDog()).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://images.dog.ceo/breeds/labrador/1.jpg"))
                .andExpect(jsonPath("$.breed").value("labrador"));
    }
}
