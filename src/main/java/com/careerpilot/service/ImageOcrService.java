package com.careerpilot.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageOcrService {

    private final ChatLanguageModel chatLanguageModel;

    public String extractTextFromImage(MultipartFile file) {
        log.info("Starting image OCR using Gemini Vision for file: {}", file.getOriginalFilename());
        try {
            String mimeType = file.getContentType();
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

            UserMessage userMessage = UserMessage.from(
                    ImageContent.from(base64Image, mimeType),
                    TextContent.from("Extract all the text from this resume image. Preserve the structure and layout as much as possible. Do not add any extra commentary, just return the text.")
            );

            String response = chatLanguageModel.generate(userMessage).content().text();
            log.info("Successfully extracted text from image");
            return response;
        } catch (Exception e) {
            log.error("Failed to perform OCR on image", e);
            if (e.getMessage() != null && (e.getMessage().contains("429") || e.getMessage().contains("QuotaFailure"))) {
                throw new com.careerpilot.exception.ai.AIUnavailableException("AI Quota limit reached (Free Tier). Please wait 1 minute and try again.", e);
            }
            throw new com.careerpilot.exception.ai.AIUnavailableException("Failed to extract text from image resume", e);
        }
    }
}
