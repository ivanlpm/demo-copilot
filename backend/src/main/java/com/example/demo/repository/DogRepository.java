package com.example.demo.repository;

import com.example.demo.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Dog entities.
 */
@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
}
