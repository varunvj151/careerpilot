package com.careerpilot.service;

import com.careerpilot.ai.RoadmapPhaseAiResult;
import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.roadmap.RoadmapPhaseDto;
import com.careerpilot.dto.roadmap.RoadmapSkillDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapAiPersonalizationService {

    private final AIOrchestrator aiOrchestrator;

    public RoadmapPhaseDto personalizePhase(String phaseName, int expectedDurationWeeks, List<RoadmapSkillDto> deterministicSkills, String jobDescription) {
        if (deterministicSkills.isEmpty()) {
            return new RoadmapPhaseDto(phaseName, expectedDurationWeeks, deterministicSkills, "No skills in this phase.");
        }

        List<String> skillNames = deterministicSkills.stream()
                .map(RoadmapSkillDto::skillName)
                .collect(Collectors.toList());

        try {
            RoadmapPhaseAiResult aiResult = aiOrchestrator.personalizeRoadmapPhase(phaseName, skillNames, jobDescription);

            List<RoadmapSkillDto> personalizedSkills = deterministicSkills.stream().map(skill -> {
                String explanation = aiResult.skillExplanations().getOrDefault(skill.skillName(), "Essential skill for this role.");
                return new RoadmapSkillDto(
                        skill.skillName(),
                        skill.classification(),
                        skill.priorityScore(),
                        skill.dependency(),
                        skill.learningHours(),
                        skill.projects(),
                        skill.resources(),
                        explanation
                );
            }).collect(Collectors.toList());

            return new RoadmapPhaseDto(
                    phaseName,
                    expectedDurationWeeks,
                    personalizedSkills,
                    aiResult.motivationalGuidance()
            );

        } catch (Exception e) {
            log.error("Failed to personalize phase {}: {}", phaseName, e.getMessage());
            // Fallback to deterministic
            return new RoadmapPhaseDto(
                    phaseName,
                    expectedDurationWeeks,
                    deterministicSkills,
                    "Focus on completing these core skills to advance your career."
            );
        }
    }
}
