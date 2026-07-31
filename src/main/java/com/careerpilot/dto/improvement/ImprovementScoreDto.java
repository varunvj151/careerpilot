package com.careerpilot.dto.improvement;

public record ImprovementScoreDto(
        int beforeScore,
        int afterScore,
        int improvementPercentage,
        int atsImprovement,
        int readabilityImprovement,
        int professionalismImprovement
) {}
