package com.careerpilot.repository;

import com.careerpilot.entity.Roadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, UUID> {

    Optional<Roadmap> findByIdAndUserId(UUID id, UUID userId);

    Optional<Roadmap> findByAnalysisIdAndUserId(UUID analysisId, UUID userId);

    boolean existsByAnalysisId(UUID analysisId);
}
