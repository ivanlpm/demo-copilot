package com.example.demo.repository;

import com.example.demo.model.Duck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for Duck entities.
 */
@Repository
public interface DuckRepository extends JpaRepository<Duck, Long> {
    
    /**
     * Find all ducks ordered by creation date (newest first).
     */
    Page<Duck> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Count ducks created after a specific date.
     */
    long countByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find the oldest duck by creation date.
     */
    Optional<Duck> findFirstByOrderByCreatedAtAsc();
    
    /**
     * Find the newest duck by creation date.
     */
    Optional<Duck> findFirstByOrderByCreatedAtDesc();
}
