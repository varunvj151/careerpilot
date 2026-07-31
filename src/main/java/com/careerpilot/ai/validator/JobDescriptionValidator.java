package com.careerpilot.ai.validator;

import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;

@Component
public class JobDescriptionValidator implements OperationValidator<ParsedJobDescriptionDto> {

    @Override
    public void validate(ParsedJobDescriptionDto response) {
        if (response == null) {
            throw new InvalidAIResponseException("Parsed JD is null");
        }
        
        boolean hasRequirements = (response.requiredSkills() != null && !response.requiredSkills().isEmpty()) ||
                                  (response.responsibilities() != null && !response.responsibilities().isEmpty());
                                  
        if (!hasRequirements) {
            throw new InvalidAIResponseException("AI extracted empty required skills and responsibilities from JD.");
        }
    }
}
