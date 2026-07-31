package com.careerpilot.ai.prompt;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SemanticMatchingPromptBuilder implements PromptBuilder<SemanticMatchingPromptBuilder.Request> {

    public record Request(String resumeText, String jdText, List<String> deterministicMissingSkills) {}

    @Override
    public String buildPrompt(Request request) {
        String missingSkillsStr = request.deterministicMissingSkills() != null && !request.deterministicMissingSkills().isEmpty() 
                ? String.join(", ", request.deterministicMissingSkills()) 
                : "None explicitly found missing.";

        return """
                You are an expert technical recruiter. Perform a deep semantic comparison between the resume and the job description.
                You are assessing Project relevance, Resume quality, Achievement quality, and Writing quality.
                
                Rules:
                1. Strengths: Generate strengths only when supported by evidence. Never generate generic praise.
                2. Weaknesses: Generate only evidence-based weaknesses (e.g. No Docker, No Testing). Never invent weaknesses.
                3. Missing Skills: Explain why the deterministic missing skills (%s) matter for this role.
                4. Recommendations: Top five resume improvements. Not free-form paragraphs. Use the requested structure.
                5. Do NOT calculate the overall score.
                
                Return only valid JSON. Do not include markdown formatting like ```json.
                
                Expected JSON format:
                {
                  "aiQualityScore": (0-100),
                  "strengths": ["...", "..."],
                  "weaknesses": ["...", "..."],
                  "missingSkills": [
                    {
                      "name": "Skill Name (from the provided missing skills)",
                      "priority": "High|Medium|Low",
                      "reason": "Why it is missing or needed"
                    }
                  ],
                  "recommendations": [
                    {
                      "title": "Short title",
                      "reason": "Why do this",
                      "priority": "High|Medium|Low",
                      "expectedImpact": "What it improves",
                      "category": "Formatting|Content|Projects|Skills|etc"
                    }
                  ]
                }
                
                Resume:
                %s
                
                Job Description:
                %s
                """.formatted(missingSkillsStr, request.resumeText(), request.jdText());
    }
}
