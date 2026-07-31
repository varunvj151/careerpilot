package com.careerpilot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.gemini")
public class AiProperties {
    private String apiKey;
    private String model = "gemini-1.5-flash";
    private Double temperature = 0.3;
    private Double topP = 0.95;
    private Integer maxOutputTokens = 8192;
    private Integer timeoutSeconds = 30;
    private Integer maxRetries = 3;
}
