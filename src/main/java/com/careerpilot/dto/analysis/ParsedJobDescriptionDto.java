package com.careerpilot.dto.analysis;

import java.util.List;

public record ParsedJobDescriptionDto(
        String jobTitle,
        String location,
        String employmentType,
        List<String> requiredSkills,
        List<String> preferredSkills,
        List<String> responsibilities,
        String minimumExperience,
        String preferredExperience,
        List<String> educationRequirements,
        List<String> technologies
) {}
