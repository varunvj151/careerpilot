package com.careerpilot.ai.prompt;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PromptBuilderTest {

    @Test
    void testResumeAnalysisPromptBuilder() {
        ResumeAnalysisPromptBuilder builder = new ResumeAnalysisPromptBuilder();
        String prompt = builder.buildPrompt("Sample resume text");
        
        assertTrue(prompt.contains("Sample resume text"));
        assertTrue(prompt.contains("Expected JSON format:"));
        assertTrue(prompt.contains("Return only valid JSON"));
    }

    @Test
    void testRoadmapPromptBuilder() {
        RoadmapPromptBuilder builder = new RoadmapPromptBuilder();
        String prompt = builder.buildPrompt(new RoadmapPromptBuilder.Request("Phase 1", List.of("Java", "Spring"), "Backend Dev"));

        assertTrue(prompt.contains("Backend Dev"));
        assertTrue(prompt.contains("Phase 1"));
        assertTrue(prompt.contains("Java, Spring"));
        assertTrue(prompt.contains("motivationalGuidance"));
    }
    
    @Test
    void testJobDescriptionPromptBuilder() {
        JobDescriptionPromptBuilder builder = new JobDescriptionPromptBuilder();
        String prompt = builder.buildPrompt("Sample JD text");
        
        assertTrue(prompt.contains("Sample JD text"));
        assertTrue(prompt.contains("Expected JSON format:"));
    }
}
