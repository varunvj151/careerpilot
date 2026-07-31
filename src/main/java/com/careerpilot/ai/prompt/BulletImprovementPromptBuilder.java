package com.careerpilot.ai.prompt;

import org.springframework.stereotype.Component;

@Component
public class BulletImprovementPromptBuilder implements PromptBuilder<BulletImprovementPromptBuilder.Request> {

    public record Request(String originalBullet, String weaknessReason, String improvementCategory, String jobDescription) {}

    @Override
    public String buildPrompt(Request request) {
        return """
                You are an expert technical resume writer. Your job is to improve a SINGLE resume bullet point.
                
                The original bullet point was flagged for the following weakness:
                Reason: %s
                Category: %s
                
                IMPORTANT RULES: 
                - NEVER invent or fabricate experience, tools, technologies, dates, or metrics.
                - Only rewrite, reformat, and emphasize existing facts to be more impactful.
                - Address the specific weakness if possible without inventing facts.
                - Ensure the tone is highly professional and action-oriented.
                
                Target Job Description Context:
                %s
                
                Original Bullet Point:
                %s
                
                Return ONLY valid JSON. Do not include markdown formatting like ```json.
                Expected JSON format:
                {
                  "improvedBullet": "The new, rewritten bullet point.",
                  "expectedAtsImpact": "A brief 1-sentence explanation of why this improves ATS matching.",
                  "confidence": 95
                }
                """.formatted(
                        request.weaknessReason(), 
                        request.improvementCategory(), 
                        request.jobDescription(), 
                        request.originalBullet()
                );
    }
}
