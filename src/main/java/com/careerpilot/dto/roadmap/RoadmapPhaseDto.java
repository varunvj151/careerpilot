package com.careerpilot.dto.roadmap;

import java.util.List;

public record RoadmapPhaseDto(
        String phaseName,
        int expectedDurationWeeks,
        List<RoadmapSkillDto> skills,
        String aiMotivationalGuidance
) {}
