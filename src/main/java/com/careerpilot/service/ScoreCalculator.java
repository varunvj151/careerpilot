package com.careerpilot.service;

import com.careerpilot.config.ScoreProperties;
import com.careerpilot.dto.analysis.AnalysisScoreDto;
import com.careerpilot.service.DeterministicAnalysisService.DeterministicMatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreCalculator {

    private final ScoreProperties scoreProperties;

    public AnalysisScoreDto calculateOverallScore(DeterministicMatchResult deterministic, int aiQualityScore) {
        
        double overall = (deterministic.requiredSkillScore() * scoreProperties.getRequiredSkills()) +
                         (deterministic.experienceScore() * scoreProperties.getExperience()) +
                         (deterministic.projectScore() * scoreProperties.getProjects()) +
                         (deterministic.educationScore() * scoreProperties.getEducation()) +
                         (deterministic.keywordCoverage() * scoreProperties.getKeywordCoverage()) +
                         (aiQualityScore * scoreProperties.getAiQuality());
                         
        return new AnalysisScoreDto(
                (int) Math.round(overall),
                deterministic.requiredSkillScore(),
                deterministic.projectScore(),
                deterministic.experienceScore(),
                deterministic.educationScore(),
                deterministic.keywordCoverage(),
                aiQualityScore
        );
    }
}
