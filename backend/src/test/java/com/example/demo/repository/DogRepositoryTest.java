package com.example.demo.repository;

import com.example.demo.model.Dog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DogRepositoryTest {

    @Autowired
    private DogRepository dogRepository;

    @Test
    @DisplayName("Should save and retrieve a Dog entity by ID")
    void shouldSaveAndFindDog() {
        // given
        Dog dog = new Dog("https://images.dog.ceo/breeds/labrador/1.jpg", "labrador");

        // when
        Dog saved = dogRepository.save(dog);

        // then
        assertThat(dogRepository.findById(saved.getId())).isPresent();
        assertThat(saved.getUrl()).isEqualTo("https://images.dog.ceo/breeds/labrador/1.jpg");
        assertThat(saved.getBreed()).isEqualTo("labrador");
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
