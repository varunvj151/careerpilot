package com.careerpilot.ai.validator;

import com.careerpilot.dto.analysis.ParsedResumeDto;
import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;

@Component
public class ResumeAnalysisValidator implements OperationValidator<ParsedResumeDto> {

    @Override
    public void validate(ParsedResumeDto response) {
        if (response == null) {
            throw new InvalidAIResponseException("Parsed resume is null");
        }
        
        // We only fail if EVERYTHING is empty, because a resume might just be terrible.
        // But if AI failed to extract anything at all, it's a bad response.
        boolean hasContent = (response.skills() != null && !response.skills().isEmpty()) ||
                             (response.experience() != null && !response.experience().isEmpty()) ||
                             (response.education() != null && !response.education().isEmpty());
                             
        if (!hasContent) {
            throw new InvalidAIResponseException("AI extracted empty skills, experience, and education from resume.");
        }
    }
}
