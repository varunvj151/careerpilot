package com.careerpilot.dto.roadmap;

public record ResourceRecommendationDto(
        String title,
        String url,
        String type // e.g. "Documentation", "Course", "Tutorial"
) {}
