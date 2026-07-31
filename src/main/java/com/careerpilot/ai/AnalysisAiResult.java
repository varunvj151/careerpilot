package com.careerpilot.ai;

import java.math.BigDecimal;
import java.util.List;

public record AnalysisAiResult(
    BigDecimal matchPercentage,
    List<String> strengths,
    List<String> weaknesses,
    List<String> matchingSkills,
    List<String> missingSkills
) {}
