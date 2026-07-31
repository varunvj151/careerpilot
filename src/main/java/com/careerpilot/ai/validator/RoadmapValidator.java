package com.careerpilot.ai.validator;

import com.careerpilot.ai.RoadmapPhaseAiResult;
import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;

@Component
public class RoadmapValidator implements OperationValidator<RoadmapPhaseAiResult> {

    @Override
    public void validate(RoadmapPhaseAiResult response) {
        if (response == null) {
            throw new InvalidAIResponseException("Roadmap Phase AI result is null");
        }
        
        if (response.motivationalGuidance() == null || response.motivationalGuidance().isBlank()) {
            throw new InvalidAIResponseException("Motivational guidance is empty");
        }
        
        if (response.skillExplanations() == null) {
            throw new InvalidAIResponseException("Skill explanations map is null");
        }
    }
}
