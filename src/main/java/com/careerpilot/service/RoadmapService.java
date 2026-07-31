package com.careerpilot.service;

import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.response.Responses.RoadmapResponse;
import com.careerpilot.entity.Analysis;
import com.careerpilot.entity.Roadmap;
import com.careerpilot.entity.User;
import com.careerpilot.repository.AnalysisRepository;
import com.careerpilot.repository.RoadmapRepository;
import com.careerpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    
    private final SkillClassificationService classificationService;
    private final PriorityEngineService priorityService;
    private final DependencyEngineService dependencyService;
    private final LearningTimeEstimatorService timeEstimatorService;
    private final RecommendationEngineService recommendationService;
    private final RoadmapAiPersonalizationService aiPersonalizationService;
    private final JobService jobService;
    
    @org.springframework.context.annotation.Lazy
    @org.springframework.beans.factory.annotation.Autowired
    private RoadmapService self;

    @Transactional
    public com.careerpilot.dto.response.Responses.JobResponse createRoadmapJob(UUID analysisId) {
        User currentUser = getCurrentUser();

        // Fast fail validation
        analysisRepository.findByIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));

        java.util.Optional<com.careerpilot.entity.AsyncJob> existingJob = jobService.findActiveJob(
                com.careerpilot.entity.JobType.ROADMAP,
                currentUser.getId()
        );
        
        com.careerpilot.entity.AsyncJob job;
        if (existingJob.isPresent()) {
            job = existingJob.get();
        } else {
            job = jobService.createJob(com.careerpilot.entity.JobType.ROADMAP);
            self.executeRoadmapJob(job.getId(), analysisId, currentUser);
        }

        return new com.careerpilot.dto.response.Responses.JobResponse(job.getId(), job.getJobType(), job.getStatus(), null, null, job.getCreatedAt(), job.getUpdatedAt());
    }

    @org.springframework.scheduling.annotation.Async
    public void executeRoadmapJob(UUID jobId, UUID analysisId, User currentUser) {
        try {
            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.RUNNING, null, null);
            log.info("Roadmap workflow started for analysisId: {}, jobId: {}", analysisId, jobId);
            
            RoadmapResponse response = self.executeRoadmapCore(jobId, analysisId, currentUser);

            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.COMPLETED, response, null);
            log.info("Roadmap generation complete for Analysis {}", analysisId);

        } catch (Exception e) {
            log.error("Roadmap job {} failed: {}", jobId, e.getMessage(), e);
            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.FAILED, null, e.getMessage());
        }
    }
    
    @Transactional
    public RoadmapResponse executeRoadmapCore(UUID jobId, UUID analysisId, User currentUser) {
        long startTime = System.currentTimeMillis();

        Analysis analysis = analysisRepository.findByIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));

        List<com.careerpilot.dto.analysis.MissingSkillDto> missingSkills = analysis.getMissingSkills();
        if (missingSkills == null || missingSkills.isEmpty()) {
            throw new IllegalArgumentException("No missing skills found in analysis. Cannot generate roadmap.");
        }

        // Phase 1-7: Process skills deterministically
        List<com.careerpilot.dto.roadmap.RoadmapSkillDto> allSkills = missingSkills.stream().map(missing -> {
            String classification = classificationService.classify(missing.name());
            boolean isRequired = "High".equalsIgnoreCase(missing.priority());
            int priority = priorityService.calculatePriority(classification, isRequired);
            String dependency = dependencyService.determineDependency(classification);
            int hours = timeEstimatorService.estimateHours(classification);
            var projects = recommendationService.recommendProjects(missing.name(), classification);
            var resources = recommendationService.recommendResources(missing.name());
            
            return new com.careerpilot.dto.roadmap.RoadmapSkillDto(
                    missing.name(), classification, priority, dependency, hours, projects, resources, ""
            );
        }).toList();

        // Group into phases based on dependency order
        Map<Integer, List<com.careerpilot.dto.roadmap.RoadmapSkillDto>> phaseMap = allSkills.stream()
                .collect(Collectors.groupingBy(s -> dependencyService.determinePhaseOrder(s.classification())));

        String jobDescriptionContext = analysis.getJobDescription().getTitle() + " - " + analysis.getJobDescription().getRawText();

        // Phase 8 & 9: Personalize phases with AI
        List<com.careerpilot.dto.roadmap.RoadmapPhaseDto> phases = phaseMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String phaseName = "Phase " + entry.getKey();
                    int expectedDuration = entry.getValue().stream().mapToInt(com.careerpilot.dto.roadmap.RoadmapSkillDto::learningHours).sum() / 40;
                    if (expectedDuration < 1) expectedDuration = 1;
                    return aiPersonalizationService.personalizePhase(phaseName, expectedDuration, entry.getValue(), jobDescriptionContext);
                })
                .toList();

        // Phase 11: Persistence
        Roadmap roadmap = Roadmap.builder()
                .user(currentUser)
                .analysis(analysis)
                .phases(phases)
                .build();

        Roadmap saved = roadmapRepository.save(roadmap);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public RoadmapResponse getRoadmap(UUID analysisId) {
        User currentUser = getCurrentUser();
        Roadmap roadmap = roadmapRepository.findByAnalysisIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Roadmap not found or access denied"));

        return mapToResponse(roadmap);
    }
    
    private RoadmapResponse mapToResponse(Roadmap roadmap) {
        return new RoadmapResponse(
                roadmap.getId(),
                roadmap.getAnalysis().getId(),
                roadmap.getPhases(),
                roadmap.getCreatedAt()
        );
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}
