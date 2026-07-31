package com.careerpilot.service;

import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDescriptionParserService {

    private final AIOrchestrator aiOrchestrator;

    private static final java.util.regex.Pattern TITLE_PATTERN = java.util.regex.Pattern.compile("(?i)Job Title:\\s*(.*?)(?=\\n|$)");
    private static final java.util.regex.Pattern LOCATION_PATTERN = java.util.regex.Pattern.compile("(?i)Location:\\s*(.*?)(?=\\n|$)");
    private static final java.util.regex.Pattern EMPLOYMENT_PATTERN = java.util.regex.Pattern.compile("(?i)Employment Type:\\s*(.*?)(?=\\n|$)");

    public ParsedJobDescriptionDto parseJobDescription(String jdText) {
        log.info("Starting job description parsing (Deterministic + AI Fallback)");
        
        String title = extractDeterministic(jdText, TITLE_PATTERN);
        String location = extractDeterministic(jdText, LOCATION_PATTERN);
        String employmentType = extractDeterministic(jdText, EMPLOYMENT_PATTERN);

        long start = System.currentTimeMillis();
        ParsedJobDescriptionDto aiResult = aiOrchestrator.extractJobDescriptionData(jdText);
        log.info("AI job description extraction completed in {} ms", (System.currentTimeMillis() - start));
        
        return new ParsedJobDescriptionDto(
                title != null ? title : aiResult.jobTitle(),
                location != null ? location : aiResult.location(),
                employmentType != null ? employmentType : aiResult.employmentType(),
                aiResult.requiredSkills(),
                aiResult.preferredSkills(),
                aiResult.responsibilities(),
                aiResult.minimumExperience(),
                aiResult.preferredExperience(),
                aiResult.educationRequirements(),
                aiResult.technologies()
        );
    }

    private String extractDeterministic(String text, java.util.regex.Pattern pattern) {
        java.util.regex.Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1).trim() : null;
        }
        return null;
    }
}
