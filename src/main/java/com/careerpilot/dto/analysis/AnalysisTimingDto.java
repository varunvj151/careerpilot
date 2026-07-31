package com.careerpilot.dto.analysis;

public record AnalysisTimingDto(
        long resumeParsingMs,
        long jdParsingMs,
        long deterministicMatchMs,
        long semanticAiMs,
        long totalTimeMs
) {}
