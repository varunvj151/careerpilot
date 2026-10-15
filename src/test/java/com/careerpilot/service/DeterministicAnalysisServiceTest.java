package com.careerpilot.service;

import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicAnalysisServiceTest {

    private DeterministicAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new DeterministicAnalysisService();
    }

    @Test
    void analyze_FullSkillMatch() {
        ParsedResumeDto resume = new ParsedResumeDto(
                "Alice", "alice@example.com", "1234567890", "", "",
                "Senior Java and Spring developer",
                List.of("Java", "Spring Boot", "PostgreSQL", "Docker"),
                List.of("B.S. in Computer Science"),
                List.of("5 years experience as software engineer"),
                List.of("Built microservices with Spring and Docker"),
                List.of(), List.of(), List.of()
        );

        ParsedJobDescriptionDto jd = new ParsedJobDescriptionDto(
                "Backend Engineer", "Remote", "Full-time",
                List.of("Java", "Spring Boot"),
                List.of("PostgreSQL"),
                List.of("Develop REST APIs", "Maintain microservices"),
                "3+ years", "5+ years",
                List.of("Computer Science"),
                List.of("Docker", "PostgreSQL")
        );

        DeterministicAnalysisService.DeterministicMatchResult result = analysisService.analyze(resume, jd);

        assertNotNull(result);
        assertEquals(100, result.requiredSkillScore());
        assertTrue(result.matchingSkills().contains("Java"));
        assertTrue(result.matchingSkills().contains("Spring Boot"));
        assertTrue(result.missingSkills().isEmpty());
    }

    @Test
    void analyze_PartialSkillMatch() {
        ParsedResumeDto resume = new ParsedResumeDto(
                "Bob", "bob@example.com", "1234567890", "", "",
                "Frontend developer",
                List.of("React", "CSS"),
                List.of("B.A. Arts"),
                List.of("2 years experience"),
                List.of("Portfolio website"),
                List.of(), List.of(), List.of()
        );

        ParsedJobDescriptionDto jd = new ParsedJobDescriptionDto(
                "Fullstack Developer", "Remote", "Full-time",
                List.of("React", "TypeScript", "Node.js", "Docker"),
                List.of(),
                List.of("Design UI"),
                "2 years", "",
                List.of("Computer Science"),
                List.of("React")
        );

        DeterministicAnalysisService.DeterministicMatchResult result = analysisService.analyze(resume, jd);

        assertNotNull(result);
        assertEquals(25, result.requiredSkillScore()); // 1 out of 4 matched = 25%
        assertTrue(result.matchingSkills().contains("React"));
        assertTrue(result.missingSkills().contains("TypeScript"));
    }
}
