package com.careerpilot.service;

import com.careerpilot.ai.DetailedAnalysisAiResult;
import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticAnalysisService {

    private final AIOrchestrator aiOrchestrator;

    public DetailedAnalysisAiResult performSemanticAnalysis(
            ParsedResumeDto resume, 
            ParsedJobDescriptionDto jd, 
            List<String> deterministicMissingSkills,
            String rawResumeText,
            String rawJdText) {
        
        log.info("Starting Semantic AI Analysis");
        
        // Use the AI orchestrator to perform semantic matching and recommendations
        // We pass the deterministic missing skills so the AI can explain them
        return aiOrchestrator.performSemanticMatching(rawResumeText, rawJdText, deterministicMissingSkills);
    }
}
