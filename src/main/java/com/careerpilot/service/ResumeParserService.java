package com.careerpilot.service;

import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParserService {

    private final AIOrchestrator aiOrchestrator;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?\\d{1,2}\\s?)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
    private static final Pattern LINKEDIN_PATTERN = Pattern.compile("(?i)(https?://(?:www\\.)?linkedin\\.com/in/[a-zA-Z0-9_-]+/?)[\\s\\S]*?");
    private static final Pattern GITHUB_PATTERN = Pattern.compile("(?i)(https?://(?:www\\.)?github\\.com/[a-zA-Z0-9_-]+/?)[\\s\\S]*?");

    public ParsedResumeDto parseResume(String rawText) {
        log.info("Starting resume parsing (Deterministic + AI fallback)");
        
        // 1. Try deterministic parsing for simple fields
        String email = extractDeterministic(rawText, EMAIL_PATTERN);
        String phone = extractDeterministic(rawText, PHONE_PATTERN);
        String linkedIn = extractDeterministic(rawText, LINKEDIN_PATTERN);
        String github = extractDeterministic(rawText, GITHUB_PATTERN);
        
        // 2. Use AI for complex fields
        long start = System.currentTimeMillis();
        ParsedResumeDto aiResult = aiOrchestrator.extractResumeData(rawText);
        log.info("AI resume extraction completed in {} ms", (System.currentTimeMillis() - start));
        
        // 3. Merge results
        return new ParsedResumeDto(
                aiResult.name(),
                email != null ? email : aiResult.email(),
                phone != null ? phone : aiResult.phone(),
                linkedIn != null ? linkedIn : aiResult.linkedIn(),
                github != null ? github : aiResult.github(),
                aiResult.summary(),
                aiResult.skills(),
                aiResult.education(),
                aiResult.experience(),
                aiResult.projects(),
                aiResult.certifications(),
                aiResult.achievements(),
                aiResult.languages()
        );
    }

    private String extractDeterministic(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1).trim() : matcher.group(0).trim();
        }
        return null;
    }
}
