package com.careerpilot.dto.analysis;

public record MissingSkillDto(
        String name,
        String priority, // e.g. "High", "Medium", "Low"
        String reason
) {}
