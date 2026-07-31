package com.careerpilot.ai.validator;

import com.careerpilot.ai.BulletImprovementAiResult;
import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BulletImprovementValidator implements OperationValidator<BulletImprovementAiResult> {

    @Override
    public void validate(BulletImprovementAiResult response) {
        if (response == null) {
            throw new InvalidAIResponseException("Bullet improvement result is null");
        }
        
        if (!StringUtils.hasText(response.improvedBullet())) {
            throw new InvalidAIResponseException("Improved bullet text is empty");
        }
        
        if (response.confidence() < 0 || response.confidence() > 100) {
            throw new InvalidAIResponseException("Confidence must be between 0 and 100");
        }
    }
}
