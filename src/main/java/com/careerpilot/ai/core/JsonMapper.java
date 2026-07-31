package com.careerpilot.ai.core;

import com.careerpilot.exception.ai.InvalidAIResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonMapper {

    private final ObjectMapper objectMapper;

    public <T> T fromJson(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (Exception e) {
            log.error("Failed to map JSON to {}: {}", targetType.getSimpleName(), e.getMessage());
            throw new InvalidAIResponseException("Failed to map AI response to DTO: " + e.getMessage(), e);
        }
    }
}
