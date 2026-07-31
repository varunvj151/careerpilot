package com.careerpilot.ai;

import com.careerpilot.dto.analysis.MissingSkillDto;
import java.util.List;

public record DetailedAnalysisAiResult(
        int aiQualityScore,
        List<String> strengths,
        List<String> weaknesses,
        List<MissingSkillDto> missingSkills,
        List<com.careerpilot.dto.analysis.RecommendationDto> recommendations
) {}
