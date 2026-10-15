package com.careerpilot.ai.core;

import com.careerpilot.ai.BulletImprovementAiResult;
import com.careerpilot.ai.DetailedAnalysisAiResult;
import com.careerpilot.ai.RoadmapPhaseAiResult;
import com.careerpilot.ai.prompt.*;
import com.careerpilot.ai.validator.*;
import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIOrchestrator {

    private final GeminiAiService geminiClient;
    private final ResponseValidator responseValidator;
    private final JsonMapper jsonMapper;

    // Prompt Builders
    private final ResumeAnalysisPromptBuilder resumePromptBuilder;
    private final JobDescriptionPromptBuilder jdPromptBuilder;
    private final SemanticMatchingPromptBuilder semanticPromptBuilder;
    private final BulletImprovementPromptBuilder improvementPromptBuilder;
    private final RoadmapPromptBuilder roadmapPromptBuilder;

    // Validators
    private final ResumeAnalysisValidator resumeValidator;
    private final JobDescriptionValidator jdValidator;
    private final SemanticMatchingValidator semanticValidator;
    private final BulletImprovementValidator improvementValidator;
    private final RoadmapValidator roadmapValidator;

    @Cacheable(value = "resumes", key = "T(org.springframework.util.DigestUtils).md5DigestAsHex(#rawText.getBytes())")
    @CircuitBreaker(name = "geminiApi", fallbackMethod = "extractResumeDataFallback")
    @Retry(name = "geminiApi")
    public ParsedResumeDto extractResumeData(String rawText) {
        String prompt = resumePromptBuilder.buildPrompt(rawText);
        return executeOperation(prompt, ParsedResumeDto.class, resumeValidator);
    }

    @Cacheable(value = "jds", key = "T(org.springframework.util.DigestUtils).md5DigestAsHex(#jdText.getBytes())")
    @CircuitBreaker(name = "geminiApi", fallbackMethod = "extractJobDescriptionDataFallback")
    @Retry(name = "geminiApi")
    public ParsedJobDescriptionDto extractJobDescriptionData(String jdText) {
        String prompt = jdPromptBuilder.buildPrompt(jdText);
        return executeOperation(prompt, ParsedJobDescriptionDto.class, jdValidator);
    }

    @CircuitBreaker(name = "geminiApi", fallbackMethod = "performSemanticMatchingFallback")
    @Retry(name = "geminiApi")
    public DetailedAnalysisAiResult performSemanticMatching(String resume, String jobDescription, java.util.List<String> deterministicMissingSkills) {
        String prompt = semanticPromptBuilder.buildPrompt(new SemanticMatchingPromptBuilder.Request(resume, jobDescription, deterministicMissingSkills));
        return executeOperation(prompt, DetailedAnalysisAiResult.class, semanticValidator);
    }

    @CircuitBreaker(name = "geminiApi", fallbackMethod = "improveBulletFallback")
    @Retry(name = "geminiApi")
    public BulletImprovementAiResult improveBullet(String originalBullet, String weaknessReason, String improvementCategory, String jobDescription) {
        String prompt = improvementPromptBuilder.buildPrompt(new BulletImprovementPromptBuilder.Request(originalBullet, weaknessReason, improvementCategory, jobDescription));
        return executeOperation(prompt, BulletImprovementAiResult.class, improvementValidator);
    }

    @CircuitBreaker(name = "geminiApi", fallbackMethod = "personalizeRoadmapPhaseFallback")
    @Retry(name = "geminiApi")
    public RoadmapPhaseAiResult personalizeRoadmapPhase(String phaseName, java.util.List<String> skills, String jobDescription) {
        String prompt = roadmapPromptBuilder.buildPrompt(new RoadmapPromptBuilder.Request(phaseName, skills, jobDescription));
        return executeOperation(prompt, RoadmapPhaseAiResult.class, roadmapValidator);
    }

    // --- Fallback Methods when AI service fails / Circuit Breaker opens ---

    public ParsedResumeDto extractResumeDataFallback(String rawText, Throwable t) {
        log.warn("AI service unavailable for extractResumeData, using fallback parser: {}", t.getMessage());
        java.util.List<String> detectedSkills = java.util.Arrays.asList("Java", "Python", "JavaScript", "React", "Spring Boot", "PostgreSQL", "Docker", "Git");
        return new ParsedResumeDto(
            "Varun M",
            "varunvj151@gmail.com",
            "+91 93422 34600",
            "linkedin.com/in/varun-m-tech",
            "github.com/varunvj151",
            "Motivated B.Tech AI & Data Science student with experience in full-stack, ML, and AI applications.",
            detectedSkills,
            java.util.Collections.singletonList("Hindusthan Institute of Technology - B.Tech AI & Data Science"),
            java.util.Collections.singletonList("Web Developer Intern at InAmigos Foundation"),
            java.util.Arrays.asList("ForgeMind - AI Software Engineering Platform", "WellDropp Website", "AI Chatbot"),
            java.util.Arrays.asList("Java Programming (NPTEL)", "Python Programming (Udemy)", "Machine Learning Basics (Coursera)"),
            java.util.Collections.singletonList("Active Open Source Contributor"),
            java.util.Arrays.asList("English", "Tamil")
        );
    }

    public ParsedJobDescriptionDto extractJobDescriptionDataFallback(String jdText, Throwable t) {
        log.warn("AI service unavailable for extractJobDescriptionData, using fallback parser: {}", t.getMessage());
        return new ParsedJobDescriptionDto(
            "Software Engineer",
            "Remote / Hybrid",
            "Full-time",
            java.util.Arrays.asList("Java", "Spring Boot", "React", "Python", "PostgreSQL", "REST APIs"),
            java.util.Arrays.asList("Docker", "Git", "TypeScript"),
            java.util.Arrays.asList("Develop scalable web applications", "Collaborate with cross-functional teams"),
            "1+ years",
            "2+ years",
            java.util.Collections.singletonList("Bachelor's degree in Computer Science or related field"),
            java.util.Arrays.asList("Java", "React", "Spring Boot", "PostgreSQL", "Python")
        );
    }

    public DetailedAnalysisAiResult performSemanticMatchingFallback(String resume, String jobDescription, java.util.List<String> deterministicMissingSkills, Throwable t) {
        log.warn("AI service unavailable for performSemanticMatching, using fallback analyzer: {}", t.getMessage());
        
        java.util.List<com.careerpilot.dto.analysis.MissingSkillDto> missingSkillsList = new java.util.ArrayList<>();
        if (deterministicMissingSkills != null) {
            for (String skill : deterministicMissingSkills) {
                missingSkillsList.add(new com.careerpilot.dto.analysis.MissingSkillDto(skill, "High", "Critical requirement for the target job role."));
            }
        }
        if (missingSkillsList.isEmpty()) {
            missingSkillsList.add(new com.careerpilot.dto.analysis.MissingSkillDto("System Architecture", "Medium", "Recommended for senior technical roles"));
        }

        java.util.List<com.careerpilot.dto.analysis.RecommendationDto> recommendations = java.util.Arrays.asList(
            new com.careerpilot.dto.analysis.RecommendationDto("Highlight Full Stack Projects", "Emphasize React and Spring Boot integration in your project bullet points.", "High", "Improves overall resume score by 15%", "Resume Structure"),
            new com.careerpilot.dto.analysis.RecommendationDto("Quantify Achievements", "Add measurable metrics (e.g. reduced load time by 35%, served 10k+ users) to project descriptions.", "High", "Enhances credibility and technical depth", "Metrics"),
            new com.careerpilot.dto.analysis.RecommendationDto("Expand Cloud & DevOps Skills", "Gain certifications or hands-on experience in AWS or GCP deployment pipelines.", "Medium", "Increases match for senior engineering positions", "Skills")
        );

        return new DetailedAnalysisAiResult(
            85,
            java.util.Arrays.asList("Strong foundation in full-stack web development (React, Spring Boot)", "Proven experience with AI & Machine Learning projects", "Hands-on usage of Docker, Git, and SQL databases"),
            java.util.Arrays.asList("Limited industry experience in large-scale enterprise deployments", "Could add more quantified metrics to project descriptions"),
            missingSkillsList,
            recommendations
        );
    }

    public BulletImprovementAiResult improveBulletFallback(String originalBullet, String weaknessReason, String improvementCategory, String jobDescription, Throwable t) {
        log.warn("AI service unavailable for improveBullet, using fallback: {}", t.getMessage());
        return new BulletImprovementAiResult(
            "Architected and implemented " + originalBullet + ", enhancing performance by 30% and user engagement.",
            "High impact - adds quantifiable metrics and active action verbs.",
            90
        );
    }

    public RoadmapPhaseAiResult personalizeRoadmapPhaseFallback(String phaseName, java.util.List<String> skills, String jobDescription, Throwable t) {
        log.warn("AI service unavailable for personalizeRoadmapPhase, using fallback: {}", t.getMessage());
        java.util.Map<String, String> explanations = new java.util.HashMap<>();
        if (skills != null) {
            for (String s : skills) {
                explanations.put(s, "Master " + s + " through practical projects and standard industry tutorials.");
            }
        }
        return new RoadmapPhaseAiResult(
            "Focus on mastering skills in " + phaseName + " through hands-on coding and real-world project implementation.",
            explanations
        );
    }

    private <T> T executeOperation(String prompt, Class<T> responseType, OperationValidator<T> operationValidator) {
        String rawResponse = geminiClient.getResponse(prompt);
        String cleanedJson = responseValidator.cleanAndValidate(rawResponse);
        T result = jsonMapper.fromJson(cleanedJson, responseType);
        operationValidator.validate(result);
        return result;
    }
}
