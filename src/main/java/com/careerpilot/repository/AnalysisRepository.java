package com.careerpilot.repository;

import com.careerpilot.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    List<Analysis> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT a FROM Analysis a JOIN FETCH a.resume JOIN FETCH a.jobDescription " +
           "WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<Analysis> findByUserIdWithDetails(UUID userId);

    Optional<Analysis> findByIdAndUserId(UUID id, UUID userId);
}
