package com.careerpilot.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SkillClassificationService {

    private static final Map<String, String> SKILL_CATEGORIES = Map.ofEntries(
            Map.entry("java", "Programming Language"),
            Map.entry("python", "Programming Language"),
            Map.entry("javascript", "Programming Language"),
            Map.entry("typescript", "Programming Language"),
            Map.entry("spring boot", "Framework"),
            Map.entry("react", "Framework"),
            Map.entry("angular", "Framework"),
            Map.entry("docker", "DevOps"),
            Map.entry("kubernetes", "DevOps"),
            Map.entry("aws", "Cloud"),
            Map.entry("azure", "Cloud"),
            Map.entry("gcp", "Cloud"),
            Map.entry("mysql", "Database"),
            Map.entry("postgresql", "Database"),
            Map.entry("mongodb", "Database")
    );

    public String classify(String skillName) {
        if (skillName == null) return "Other";
        return SKILL_CATEGORIES.getOrDefault(skillName.toLowerCase(), "Other");
    }
}
