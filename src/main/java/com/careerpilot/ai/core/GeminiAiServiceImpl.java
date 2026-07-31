package com.careerpilot.ai.core;

import com.careerpilot.exception.ai.AITimeoutException;
import com.careerpilot.exception.ai.AIUnavailableException;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiAiServiceImpl implements GeminiAiService {

    private final ChatLanguageModel chatLanguageModel;

    @Override
    public String getResponse(String prompt) {
        log.info("Starting AI request...");
        long start = System.currentTimeMillis();
        try {
            String response = chatLanguageModel.generate(prompt);
            log.info("AI request completed in {} ms", (System.currentTimeMillis() - start));
            return response;
        } catch (Exception ex) {
            log.error("Error communicating with Gemini: {}", ex.getMessage());
            String errorMessage = ex.getMessage();
            
            if (errorMessage != null && (errorMessage.toLowerCase().contains("timeout") || errorMessage.toLowerCase().contains("timed out"))) {
                throw new AITimeoutException("AI request timed out.", ex);
            }
            if (errorMessage != null && (errorMessage.contains("429") || errorMessage.contains("503") || errorMessage.contains("Unavailable"))) {
                throw new AIUnavailableException("AI service is temporarily unavailable.", ex);
            }
            // For other exceptions, rethrow as generic AI Unavailable or a generic AI Service exception
            throw new AIUnavailableException("Failed to get response from AI.", ex);
        }
    }
}
