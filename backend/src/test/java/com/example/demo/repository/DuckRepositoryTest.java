package com.example.demo.repository;

import com.example.demo.model.Duck;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DuckRepositoryTest {

    @Autowired
    private DuckRepository duckRepository;

    @Test
    @DisplayName("should save and find duck by id")
    void saveAndFindDuck() {
        // given
        Duck duck = new Duck("https://duck.com/test.jpg", "Test Duck");

        // when
        Duck savedDuck = duckRepository.save(duck);

        // then
        assertThat(savedDuck.getId()).isNotNull();
        Optional<Duck> foundDuck = duckRepository.findById(savedDuck.getId());
        assertThat(foundDuck).isPresent();
        assertThat(foundDuck.get().getUrl()).isEqualTo("https://duck.com/test.jpg");
        assertThat(foundDuck.get().getCreatedAt()).isNotNull();
    }
}
