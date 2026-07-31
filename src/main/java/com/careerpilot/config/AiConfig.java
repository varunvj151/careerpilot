package com.careerpilot.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    private final AiProperties aiProperties;

    @Bean
    public ChatLanguageModel geminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(aiProperties.getApiKey())
                .modelName(aiProperties.getModel())
                .temperature(aiProperties.getTemperature())
                .topP(aiProperties.getTopP())
                .maxOutputTokens(aiProperties.getMaxOutputTokens())
                .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
                .build();
    }
}
