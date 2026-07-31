package com.careerpilot.repository;

import com.careerpilot.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, UUID> {

    List<JobDescription> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<JobDescription> findByIdAndUserId(UUID id, UUID userId);
}
