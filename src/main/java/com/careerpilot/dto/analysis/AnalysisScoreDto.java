package com.careerpilot.dto.analysis;

public record AnalysisScoreDto(
        int overallScore,
        int skillsScore,
        int projectScore,
        int experienceScore,
        int educationScore,
        int keywordCoverage,
        int aiQualityScore
) {}
