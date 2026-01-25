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
 * Entity representing a duck record saved in the database.
 */
@Entity
@Table(name = "ducks")
@Data
@NoArgsConstructor
public class Duck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String message;
    private LocalDateTime createdAt;

    public Duck(String url, String message) {
        this.url = url;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
