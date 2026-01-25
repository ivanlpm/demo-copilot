package com.example.demo.repository;

import com.example.demo.model.Duck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Duck entities.
 */
@Repository
public interface DuckRepository extends JpaRepository<Duck, Long> {
}
