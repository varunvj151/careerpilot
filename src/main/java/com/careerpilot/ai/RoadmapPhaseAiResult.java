package com.careerpilot.ai;

import java.util.Map;

public record RoadmapPhaseAiResult(
        String motivationalGuidance,
        Map<String, String> skillExplanations // Maps skillName -> AI explanation
) {}
