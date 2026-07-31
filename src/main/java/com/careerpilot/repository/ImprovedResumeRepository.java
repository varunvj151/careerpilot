package com.careerpilot.repository;

import com.careerpilot.entity.ImprovedResume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImprovedResumeRepository extends JpaRepository<ImprovedResume, UUID> {

    Optional<ImprovedResume> findByIdAndUserId(UUID id, UUID userId);

    Optional<ImprovedResume> findByAnalysisIdAndUserId(UUID analysisId, UUID userId);

    boolean existsByAnalysisId(UUID analysisId);
}
