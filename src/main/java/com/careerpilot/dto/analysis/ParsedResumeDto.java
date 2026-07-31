package com.careerpilot.dto.analysis;

import java.util.List;

public record ParsedResumeDto(
        String name,
        String email,
        String phone,
        String linkedIn,
        String github,
        String summary,
        List<String> skills,
        List<String> education,
        List<String> experience,
        List<String> projects,
        List<String> certifications,
        List<String> achievements,
        List<String> languages
) {}
