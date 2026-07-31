package com.careerpilot.service;

import com.careerpilot.ai.DetailedAnalysisAiResult;
import com.careerpilot.dto.analysis.AnalysisTimingDto;
import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import com.careerpilot.dto.analysis.AnalysisScoreDto;
import com.careerpilot.dto.request.AnalysisRequest;
import com.careerpilot.dto.response.Responses.*;
import com.careerpilot.entity.Analysis;
import com.careerpilot.entity.AsyncJob;
import com.careerpilot.entity.JobStatus;
import com.careerpilot.entity.JobType;
import com.careerpilot.entity.JobDescription;
import com.careerpilot.entity.Resume;
import com.careerpilot.entity.User;
import com.careerpilot.repository.AnalysisRepository;
import com.careerpilot.repository.JobDescriptionRepository;
import com.careerpilot.repository.ResumeRepository;
import com.careerpilot.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final ResumeRepository resumeRepository;
    private final JobDescriptionRepository jobDescriptionRepository;
    private final UserRepository userRepository;
    
    private final ResumeParserService resumeParserService;
    private final JobDescriptionParserService jdParserService;
    private final DeterministicAnalysisService deterministicAnalysisService;
    private final SemanticAnalysisService semanticAnalysisService;
    private final ScoreCalculator scoreCalculator;
    private final JobService jobService;
    
    private final ObjectMapper objectMapper;
    
    @org.springframework.context.annotation.Lazy
    @org.springframework.beans.factory.annotation.Autowired
    private AnalysisService self;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String aiModel;
    private static final String ANALYSIS_VERSION = "2.0";
    
    @Transactional
    public JobResponse createAnalysisJob(AnalysisRequest request) {
        User currentUser = getCurrentUser();
        
        // Fast fail validation
        resumeRepository.findByIdAndUserId(request.resumeId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or access denied"));
        
        AsyncJob job = jobService.createJob(JobType.ANALYSIS);
        
        // Trigger async execution
        self.executeAnalysisJob(job.getId(), request, currentUser);
        
        return new JobResponse(job.getId(), job.getJobType(), job.getStatus(), null, null, job.getCreatedAt(), job.getUpdatedAt());
    }

    @org.springframework.scheduling.annotation.Async
    public void executeAnalysisJob(UUID jobId, AnalysisRequest request, User currentUser) {
        try {
            jobService.updateJobStatus(jobId, JobStatus.RUNNING, null, null);
            log.info("Analysis workflow started for resumeId: {}, jobId: {}", request.resumeId(), jobId);
            
            AnalysisResponse response = self.executeAnalysisCore(jobId, request, currentUser);
            
            jobService.updateJobStatus(jobId, JobStatus.COMPLETED, response, null);
            log.info("Analysis workflow completed successfully for analysisId: {}", response.id());
            
        } catch (Exception e) {
            log.error("Analysis job {} failed: {}", jobId, e.getMessage(), e);
            jobService.updateJobStatus(jobId, JobStatus.FAILED, null, e.getMessage());
        }
    }
    
    @Transactional
    public AnalysisResponse executeAnalysisCore(UUID jobId, AnalysisRequest request, User currentUser) {
        long totalStart = System.currentTimeMillis();

        Resume resume = resumeRepository.findByIdAndUserId(request.resumeId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Resume not found or access denied"));

        JobDescription jobDescription = JobDescription.builder()
                .user(currentUser)
                .rawText(request.jobDescriptionText())
                .title(request.jobTitle())
                .company(request.company())
                .build();
        
        JobDescription savedJobDescription = jobDescriptionRepository.save(jobDescription);

        // 1. Parse Resume
        long p1Start = System.currentTimeMillis();
        ParsedResumeDto parsedResume = resumeParserService.parseResume(resume.getRawText());
        long p1Time = System.currentTimeMillis() - p1Start;

        // 2. Parse JD
        long p2Start = System.currentTimeMillis();
        ParsedJobDescriptionDto parsedJd = jdParserService.parseJobDescription(request.jobDescriptionText());
        long p2Time = System.currentTimeMillis() - p2Start;

        // 3. Deterministic Match
        long p3Start = System.currentTimeMillis();
        DeterministicAnalysisService.DeterministicMatchResult deterministic = deterministicAnalysisService.analyze(parsedResume, parsedJd);
        long p3Time = System.currentTimeMillis() - p3Start;
        
        // 4. Semantic Match
        long p4Start = System.currentTimeMillis();
        DetailedAnalysisAiResult semantic = semanticAnalysisService.performSemanticAnalysis(
                parsedResume, parsedJd, deterministic.missingSkills(), resume.getRawText(), request.jobDescriptionText());
        long p4Time = System.currentTimeMillis() - p4Start;

        // 5. Compute Final Score
        AnalysisScoreDto finalScores = scoreCalculator.calculateOverallScore(deterministic, semantic.aiQualityScore());

        long totalTime = System.currentTimeMillis() - totalStart;
        AnalysisTimingDto timing = new AnalysisTimingDto(p1Time, p2Time, p3Time, p4Time, totalTime);

        Analysis analysis = Analysis.builder()
                .user(currentUser)
                .resume(resume)
                .jobDescription(savedJobDescription)
                .matchPercentage(new BigDecimal(finalScores.overallScore()))
                .matchingSkills(deterministic.matchingSkills())
                .missingSkills(semantic.missingSkills())
                .strengths(semantic.strengths())
                .weaknesses(semantic.weaknesses())
                .extractedResume(parsedResume)
                .extractedJd(parsedJd)
                .scores(finalScores)
                .recommendations(semantic.recommendations())
                .timing(timing)
                .aiModel(aiModel)
                .analysisVersion(ANALYSIS_VERSION)
                .build();

        try {
            analysis.setRawAiResponse(objectMapper.writeValueAsString(semantic));
        } catch (Exception e) {
            log.warn("Failed to serialize raw AI response", e);
        }

        Analysis savedAnalysis = analysisRepository.save(analysis);
        return mapToResponse(savedAnalysis);
    }

    @Transactional(readOnly = true)
    public AnalysisResponse getAnalysis(UUID id) {
        User currentUser = getCurrentUser();
        Analysis analysis = analysisRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));
        
        return mapToResponse(analysis);
    }

    @Transactional(readOnly = true)
    public List<AnalysisResponse> getAnalysisHistory() {
        User currentUser = getCurrentUser();
        return analysisRepository.findByUserIdWithDetails(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAnalysis(UUID id) {
        User currentUser = getCurrentUser();
        Analysis analysis = analysisRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Analysis not found or access denied"));
        analysisRepository.delete(analysis);
        log.info("Analysis deleted successfully: {}", id);
    }

    private AnalysisResponse mapToResponse(Analysis analysis) {
        ResumeResponse resumeResponse = new ResumeResponse(
                analysis.getResume().getId(),
                analysis.getResume().getFileName(),
                analysis.getResume().getUploadedAt()
        );

        JobDescriptionResponse jdResponse = new JobDescriptionResponse(
                analysis.getJobDescription().getId(),
                analysis.getJobDescription().getTitle(),
                analysis.getJobDescription().getCompany(),
                analysis.getJobDescription().getCreatedAt()
        );

        return new AnalysisResponse(
                analysis.getId(),
                resumeResponse,
                jdResponse,
                analysis.getMatchPercentage(),
                analysis.getMatchingSkills(),
                analysis.getMissingSkills(),
                analysis.getStrengths(),
                analysis.getWeaknesses(),
                analysis.getRecommendations(),
                analysis.getExtractedResume(),
                analysis.getExtractedJd(),
                analysis.getScores(),
                analysis.getTiming(),
                analysis.getAiModel(),
                analysis.getAnalysisVersion(),
                analysis.getCreatedAt()
        );
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}
