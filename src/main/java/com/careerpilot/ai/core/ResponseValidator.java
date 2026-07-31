package com.careerpilot.ai.core;

import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResponseValidator {

    public String cleanAndValidate(String rawResponse) {
        if (!StringUtils.hasText(rawResponse)) {
            throw new InvalidAIResponseException("AI response is empty");
        }

        String cleaned = rawResponse.trim();
        
        // Remove markdown formatting if the LLM includes it
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring("```json".length());
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring("```".length());
        }
        
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }
        
        cleaned = cleaned.trim();

        if (!cleaned.startsWith("{") && !cleaned.startsWith("[")) {
            throw new InvalidAIResponseException("AI response is not a valid JSON object or array. First char: " + cleaned.charAt(0));
        }
        
        return cleaned;
    }
}
