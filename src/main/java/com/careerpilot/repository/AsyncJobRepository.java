package com.careerpilot.repository;

import com.careerpilot.entity.AsyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AsyncJobRepository extends JpaRepository<AsyncJob, UUID> {
    Optional<AsyncJob> findByIdAndUserId(UUID id, UUID userId);
    
    Optional<AsyncJob> findFirstByUserIdAndJobTypeAndStatusIn(UUID userId, com.careerpilot.entity.JobType jobType, java.util.List<com.careerpilot.entity.JobStatus> statuses);
}
