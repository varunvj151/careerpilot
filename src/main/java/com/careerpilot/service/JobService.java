package com.careerpilot.service;

import com.careerpilot.dto.response.Responses.JobResponse;
import com.careerpilot.entity.AsyncJob;
import com.careerpilot.entity.JobStatus;
import com.careerpilot.entity.JobType;
import com.careerpilot.entity.User;
import com.careerpilot.repository.AsyncJobRepository;
import com.careerpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final AsyncJobRepository asyncJobRepository;
    private final UserRepository userRepository;

    @Transactional
    public AsyncJob createJob(JobType jobType) {
        User currentUser = getCurrentUser();
        
        java.util.Optional<AsyncJob> existingJob = asyncJobRepository.findFirstByUserIdAndJobTypeAndStatusIn(
                currentUser.getId(), 
                jobType, 
                java.util.List.of(JobStatus.PENDING, JobStatus.RUNNING)
        );
        
        if (existingJob.isPresent()) {
            return existingJob.get(); // Return the existing job to prevent duplicates
        }

        AsyncJob job = AsyncJob.builder()
                .user(currentUser)
                .jobType(jobType)
                .status(JobStatus.PENDING)
                .build();
        return asyncJobRepository.save(job);
    }
    
    @Transactional(readOnly = true)
    public java.util.Optional<AsyncJob> findActiveJob(JobType jobType, UUID userId) {
        return asyncJobRepository.findFirstByUserIdAndJobTypeAndStatusIn(
                userId, 
                jobType, 
                java.util.List.of(JobStatus.PENDING, JobStatus.RUNNING)
        );
    }
    
    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @Transactional
    public void recoverStuckJobs() {
        log.info("Checking for stuck async jobs...");
        java.util.List<AsyncJob> allJobs = asyncJobRepository.findAll();
        for (AsyncJob job : allJobs) {
            if (job.getStatus() == JobStatus.RUNNING) {
                log.warn("Found stuck job {}, marking as FAILED due to server restart", job.getId());
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage("Job interrupted due to server restart");
                asyncJobRepository.save(job);
            }
        }
    }
    
    @Transactional
    public void updateJobStatus(UUID jobId, JobStatus status, Object resultPayload, String errorMessage) {
        AsyncJob job = asyncJobRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setStatus(status);
            job.setResultPayload(resultPayload);
            job.setErrorMessage(errorMessage);
            asyncJobRepository.save(job);
        }
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(UUID id) {
        User currentUser = getCurrentUser();
        AsyncJob job = asyncJobRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found or access denied"));
        return new JobResponse(
                job.getId(),
                job.getJobType(),
                job.getStatus(),
                job.getResultPayload(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}
