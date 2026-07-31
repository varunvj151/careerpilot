package com.careerpilot.service;

import com.careerpilot.dto.improvement.BulletDto;
import com.careerpilot.dto.improvement.BulletImprovementDto;
import com.careerpilot.dto.improvement.ImprovementScoreDto;
import com.careerpilot.dto.response.Responses.ImprovementResponse;
import com.careerpilot.entity.Analysis;
import com.careerpilot.entity.ImprovedResume;
import com.careerpilot.entity.User;
import com.careerpilot.repository.AnalysisRepository;
import com.careerpilot.repository.ImprovedResumeRepository;
import com.careerpilot.repository.UserRepository;
import com.careerpilot.service.ImprovementRuleEngineService.WeakBullet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImprovementService {

    private final ImprovedResumeRepository improvedResumeRepository;
    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;
    private final ImprovementRuleEngineService ruleEngineService;
    private final BulletImprovementAiService aiService;
    private final JobService jobService;
    
    @org.springframework.context.annotation.Lazy
    @org.springframework.beans.factory.annotation.Autowired
    private ImprovementService self;

    @Transactional
    public com.careerpilot.dto.response.Responses.JobResponse createImprovementJob(UUID analysisId) {
        User currentUser = getCurrentUser();

        // Fast fail validation
        analysisRepository.findByIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));

        java.util.Optional<com.careerpilot.entity.AsyncJob> existingJob = jobService.findActiveJob(
                com.careerpilot.entity.JobType.IMPROVEMENT,
                currentUser.getId()
        );
        
        com.careerpilot.entity.AsyncJob job;
        if (existingJob.isPresent()) {
            job = existingJob.get();
        } else {
            job = jobService.createJob(com.careerpilot.entity.JobType.IMPROVEMENT);
            self.executeImprovementJob(job.getId(), analysisId, currentUser);
        }

        return new com.careerpilot.dto.response.Responses.JobResponse(job.getId(), job.getJobType(), job.getStatus(), null, null, job.getCreatedAt(), job.getUpdatedAt());
    }

    @org.springframework.scheduling.annotation.Async
    public void executeImprovementJob(UUID jobId, UUID analysisId, User currentUser) {
        try {
            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.RUNNING, null, null);
            log.info("Improvement workflow started for analysisId: {}, jobId: {}", analysisId, jobId);

            ImprovementResponse response = self.executeImprovementCore(jobId, analysisId, currentUser);

            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.COMPLETED, response, null);
            log.info("Improvement workflow completed successfully for analysisId: {}", analysisId);

        } catch (Exception e) {
            log.error("Improvement job {} failed: {}", jobId, e.getMessage(), e);
            jobService.updateJobStatus(jobId, com.careerpilot.entity.JobStatus.FAILED, null, e.getMessage());
        }
    }
    
    @Transactional
    public ImprovementResponse executeImprovementCore(UUID jobId, UUID analysisId, User currentUser) {
        Analysis analysis = analysisRepository.findByIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));

        String jobDescription = analysis.getJobDescription().getRawText();

        // 1 & 2. Extract Bullets
        List<BulletDto> bullets = extractBullets(analysis.getExtractedResume());

        // 3. Weak Content Detection (Deterministic)
        List<WeakBullet> weakBullets = ruleEngineService.evaluateBullets(bullets);

        // 4. AI Rewrite (for weak bullets only)
        List<BulletImprovementDto> improvements = aiService.generateImprovements(weakBullets, jobDescription);

        // 5. Calculate Score
        ImprovementScoreDto scores = calculateScores(bullets.size(), improvements);

        // 6. Persistence
        ImprovedResume improvedResume = ImprovedResume.builder()
                .user(currentUser)
                .analysis(analysis)
                .summary(analysis.getExtractedResume().summary())
                .improvements(improvements)
                .scores(scores)
                .build();

        ImprovedResume saved = improvedResumeRepository.save(improvedResume);

        return new ImprovementResponse(
                saved.getId(),
                saved.getAnalysis().getId(),
                saved.getSummary(),
                saved.getImprovements(),
                saved.getScores(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ImprovementResponse getImprovement(UUID analysisId) {
        User currentUser = getCurrentUser();
        ImprovedResume improvedResume = improvedResumeRepository.findByAnalysisIdAndUserId(analysisId, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Improvement not found or access denied"));

        return new ImprovementResponse(
                improvedResume.getId(),
                improvedResume.getAnalysis().getId(),
                improvedResume.getSummary(),
                improvedResume.getImprovements(),
                improvedResume.getScores(),
                improvedResume.getCreatedAt()
        );
    }

    private List<BulletDto> extractBullets(com.careerpilot.dto.analysis.ParsedResumeDto parsedResume) {
        List<BulletDto> bullets = new ArrayList<>();
        int pos = 0;
        
        if (parsedResume.experience() != null) {
            for (String exp : parsedResume.experience()) {
                bullets.add(new BulletDto(UUID.randomUUID(), "Experience", exp, pos++, exp.split("\\s+").length));
            }
        }
        
        if (parsedResume.projects() != null) {
            for (String proj : parsedResume.projects()) {
                bullets.add(new BulletDto(UUID.randomUUID(), "Projects", proj, pos++, proj.split("\\s+").length));
            }
        }
        
        return bullets;
    }

    private ImprovementScoreDto calculateScores(int totalBullets, List<BulletImprovementDto> improvements) {
        int improvedCount = improvements.size();
        int improvementPercentage = totalBullets == 0 ? 0 : (improvedCount * 100) / totalBullets;
        
        // Simple deterministic scoring based on categories
        int atsImp = 0;
        int readImp = 0;
        int profImp = 0;

        for (BulletImprovementDto imp : improvements) {
            if (imp.category().contains("Action") || imp.category().contains("Metrics")) profImp += 5;
            if (imp.category().contains("Readability")) readImp += 5;
            if (imp.expectedAtsImpact() != null && !imp.expectedAtsImpact().isEmpty()) atsImp += 5;
        }

        return new ImprovementScoreDto(
                50, // mock before score
                50 + improvementPercentage, // mock after score
                improvementPercentage,
                Math.min(100, atsImp),
                Math.min(100, readImp),
                Math.min(100, profImp)
        );
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}
