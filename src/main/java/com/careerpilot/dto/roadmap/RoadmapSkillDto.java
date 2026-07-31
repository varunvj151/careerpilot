package com.careerpilot.dto.roadmap;

import java.util.List;

public record RoadmapSkillDto(
        String skillName,
        String classification,
        int priorityScore,
        String dependency,
        int learningHours,
        List<ProjectRecommendationDto> projects,
        List<ResourceRecommendationDto> resources,
        String aiExplanation
) {}
