package com.careerpilot.ai.validator;

import com.careerpilot.ai.DetailedAnalysisAiResult;
import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;

@Component
public class SemanticMatchingValidator implements OperationValidator<DetailedAnalysisAiResult> {

    @Override
    public void validate(DetailedAnalysisAiResult response) {
        if (response == null) {
            throw new InvalidAIResponseException("Detailed analysis result is null");
        }
        
        validateScore(response.aiQualityScore(), "aiQualityScore");
        
        if (response.recommendations() == null || response.recommendations().isEmpty()) {
            throw new InvalidAIResponseException("Recommendations list is empty or null");
        }
    }
    
    private void validateScore(int score, String fieldName) {
        if (score < 0 || score > 100) {
            throw new InvalidAIResponseException("Invalid score for " + fieldName + ": " + score + ". Must be between 0 and 100.");
        }
    }
}
