package com.careerpilot.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.careerpilot.entity.JobStatus;
import com.careerpilot.entity.JobType;

public final class Responses {

    private Responses() {}

    // ── Jobs ────────────────────────────────────────────────────────────────
    public record JobResponse(
            UUID id,
            JobType jobType,
            JobStatus status,
            Object resultPayload, // Will be cast based on status and jobType
            String errorMessage,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // ── Auth ────────────────────────────────────────────────────────────────
    public record AuthResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            UserResponse user
    ) {
        public static String TOKEN_TYPE = "Bearer";
    }

    // ── User ────────────────────────────────────────────────────────────────
    public record UserResponse(
            UUID id,
            String email,
            String fullName,
            LocalDateTime createdAt
    ) {}

    // ── Resume ──────────────────────────────────────────────────────────────
    public record ResumeResponse(
            UUID id,
            String fileName,
            LocalDateTime uploadedAt
    ) {}

    // ── Analysis ────────────────────────────────────────────────────────────
    public record AnalysisResponse(
            UUID id,
            ResumeResponse resume,
            JobDescriptionResponse jobDescription,
            BigDecimal matchPercentage,
            List<String> matchingSkills,
            List<com.careerpilot.dto.analysis.MissingSkillDto> missingSkills,
            List<String> strengths,
            List<String> weaknesses,
            List<com.careerpilot.dto.analysis.RecommendationDto> recommendations,
            com.careerpilot.dto.analysis.ParsedResumeDto extractedResume,
            com.careerpilot.dto.analysis.ParsedJobDescriptionDto extractedJd,
            com.careerpilot.dto.analysis.AnalysisScoreDto scores,
            com.careerpilot.dto.analysis.AnalysisTimingDto timing,
            String aiModel,
            String analysisVersion,
            LocalDateTime createdAt
    ) {}

    public record JobDescriptionResponse(
            UUID id,
            String title,
            String company,
            LocalDateTime createdAt
    ) {}

    // ── Improvement ─────────────────────────────────────────────────────────
    public record ImprovementResponse(
            UUID id,
            UUID analysisId,
            String summary,
            List<com.careerpilot.dto.improvement.BulletImprovementDto> improvements,
            com.careerpilot.dto.improvement.ImprovementScoreDto scores,
            LocalDateTime createdAt
    ) {}

    // ── Roadmap ─────────────────────────────────────────────────────────────
    public record RoadmapResponse(
            UUID id,
            UUID analysisId,
            List<com.careerpilot.dto.roadmap.RoadmapPhaseDto> phases,
            LocalDateTime createdAt
    ) {}

    // ── Error ────────────────────────────────────────────────────────────────
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}

    // ── Validation Error ─────────────────────────────────────────────────────
    public record ValidationErrorResponse(
            int status,
            String error,
            List<FieldError> fieldErrors,
            LocalDateTime timestamp
    ) {}

    public record FieldError(
            String field,
            String message
    ) {}
}
