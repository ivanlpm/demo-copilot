package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a dog record saved in the database.
 */
@Entity
@Table(name = "dogs")
@Data
@NoArgsConstructor
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String breed;
    private LocalDateTime createdAt;

    public Dog(String url, String breed) {
        this.url = url;
        this.breed = breed;
        this.createdAt = LocalDateTime.now();
    }
}
