package com.careerpilot.dto.analysis;

public record RecommendationDto(
        String title,
        String reason,
        String priority,
        String expectedImpact,
        String category
) {}
