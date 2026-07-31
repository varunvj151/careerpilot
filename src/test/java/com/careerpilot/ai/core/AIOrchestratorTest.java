package com.careerpilot.ai.core;

import com.careerpilot.ai.prompt.*;
import com.careerpilot.ai.validator.*;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import com.careerpilot.ai.RoadmapPhaseAiResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AIOrchestratorTest {

    private AIOrchestrator orchestrator;
    private GeminiAiService geminiClient;
    private ResponseValidator responseValidator;
    private JsonMapper jsonMapper;

    private ResumeAnalysisPromptBuilder resumePromptBuilder;
    private ResumeAnalysisValidator resumeValidator;

    @BeforeEach
    void setUp() {
        geminiClient = Mockito.mock(GeminiAiService.class);
        responseValidator = Mockito.mock(ResponseValidator.class);
        jsonMapper = Mockito.mock(JsonMapper.class);
        
        resumePromptBuilder = Mockito.mock(ResumeAnalysisPromptBuilder.class);
        resumeValidator = Mockito.mock(ResumeAnalysisValidator.class);
        
        // Setup the orchestrator with just the dependencies we need for testing resume extraction
        orchestrator = new AIOrchestrator(
                geminiClient, responseValidator, jsonMapper,
                resumePromptBuilder,
                Mockito.mock(JobDescriptionPromptBuilder.class),
                Mockito.mock(SemanticMatchingPromptBuilder.class),
                Mockito.mock(BulletImprovementPromptBuilder.class),
                Mockito.mock(RoadmapPromptBuilder.class),
                resumeValidator,
                Mockito.mock(JobDescriptionValidator.class),
                Mockito.mock(SemanticMatchingValidator.class),
                Mockito.mock(BulletImprovementValidator.class),
                Mockito.mock(RoadmapValidator.class)
        );
    }

    @Test
    void testExtractResumeData() {
        String rawText = "My Resume";
        String prompt = "Build prompt";
        String rawResponse = "{}";
        String cleanJson = "{}";
        ParsedResumeDto dto = new ParsedResumeDto("Test", "test@example.com", null, null, null, null, null, null, null, null, null, null, null);

        when(resumePromptBuilder.buildPrompt(rawText)).thenReturn(prompt);
        when(geminiClient.getResponse(prompt)).thenReturn(rawResponse);
        when(responseValidator.cleanAndValidate(rawResponse)).thenReturn(cleanJson);
        when(jsonMapper.fromJson(cleanJson, ParsedResumeDto.class)).thenReturn(dto);
        doNothing().when(resumeValidator).validate(dto);

        ParsedResumeDto result = orchestrator.extractResumeData(rawText);
        
        assertNotNull(result);
        verify(resumePromptBuilder).buildPrompt(rawText);
        verify(geminiClient).getResponse(prompt);
        verify(responseValidator).cleanAndValidate(rawResponse);
        verify(jsonMapper).fromJson(cleanJson, ParsedResumeDto.class);
        verify(resumeValidator).validate(dto);
    }
}
