package com.careerpilot.ai.prompt;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoadmapPromptBuilder implements PromptBuilder<RoadmapPromptBuilder.Request> {

    public record Request(String phaseName, List<String> skills, String jobDescription) {}

    @Override
    public String buildPrompt(Request request) {
        return """
                You are a senior engineering manager and technical mentor. We have deterministically built a learning roadmap for a candidate based on their missing skills for a target job description.
                
                Your job is to provide PERSONALIZATION for a single learning Phase.
                Do NOT invent new skills, change learning times, or alter dependencies.
                
                Target Job Description:
                %s
                
                Current Phase: %s
                Skills in this Phase: %s
                
                Return ONLY valid JSON. Do not include markdown formatting like ```json.
                Expected JSON format:
                {
                  "motivationalGuidance": "A 2-3 sentence encouraging, personalized message explaining why this phase is important for their career goals.",
                  "skillExplanations": {
                    "Skill1": "A 1-sentence explanation of why this specific skill matters for the target job.",
                    "Skill2": "A 1-sentence explanation..."
                  }
                }
                """.formatted(request.jobDescription(), request.phaseName(), String.join(", ", request.skills()));
    }
}
