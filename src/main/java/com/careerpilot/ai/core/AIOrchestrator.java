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
    @CircuitBreaker(name = "geminiApi")
    @Retry(name = "geminiApi")
    public ParsedResumeDto extractResumeData(String rawText) {
        String prompt = resumePromptBuilder.buildPrompt(rawText);
        return executeOperation(prompt, ParsedResumeDto.class, resumeValidator);
    }

    @Cacheable(value = "jds", key = "T(org.springframework.util.DigestUtils).md5DigestAsHex(#jdText.getBytes())")
    @CircuitBreaker(name = "geminiApi")
    @Retry(name = "geminiApi")
    public ParsedJobDescriptionDto extractJobDescriptionData(String jdText) {
        String prompt = jdPromptBuilder.buildPrompt(jdText);
        return executeOperation(prompt, ParsedJobDescriptionDto.class, jdValidator);
    }

    @CircuitBreaker(name = "geminiApi")
    @Retry(name = "geminiApi")
    public DetailedAnalysisAiResult performSemanticMatching(String resume, String jobDescription, java.util.List<String> deterministicMissingSkills) {
        String prompt = semanticPromptBuilder.buildPrompt(new SemanticMatchingPromptBuilder.Request(resume, jobDescription, deterministicMissingSkills));
        return executeOperation(prompt, DetailedAnalysisAiResult.class, semanticValidator);
    }

    @CircuitBreaker(name = "geminiApi")
    @Retry(name = "geminiApi")
    public BulletImprovementAiResult improveBullet(String originalBullet, String weaknessReason, String improvementCategory, String jobDescription) {
        String prompt = improvementPromptBuilder.buildPrompt(new BulletImprovementPromptBuilder.Request(originalBullet, weaknessReason, improvementCategory, jobDescription));
        return executeOperation(prompt, BulletImprovementAiResult.class, improvementValidator);
    }

    @CircuitBreaker(name = "geminiApi")
    @Retry(name = "geminiApi")
    public RoadmapPhaseAiResult personalizeRoadmapPhase(String phaseName, java.util.List<String> skills, String jobDescription) {
        String prompt = roadmapPromptBuilder.buildPrompt(new RoadmapPromptBuilder.Request(phaseName, skills, jobDescription));
        return executeOperation(prompt, RoadmapPhaseAiResult.class, roadmapValidator);
    }

    private <T> T executeOperation(String prompt, Class<T> responseType, OperationValidator<T> operationValidator) {
        String rawResponse = geminiClient.getResponse(prompt);
        String cleanedJson = responseValidator.cleanAndValidate(rawResponse);
        T result = jsonMapper.fromJson(cleanedJson, responseType);
        operationValidator.validate(result);
        return result;
    }
}
