package com.careerpilot.service;

import com.careerpilot.config.ScoreProperties;
import com.careerpilot.dto.analysis.AnalysisScoreDto;
import com.careerpilot.service.DeterministicAnalysisService.DeterministicMatchResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreCalculatorTest {

    @Test
    void shouldCalculateWeightedOverallScore() {
        // Arrange
        ScoreProperties properties = new ScoreProperties();
        // default weights: requiredSkills(0.35), experience(0.20), projects(0.15), education(0.10), keywordCoverage(0.10), aiQuality(0.10)
        ScoreCalculator scoreCalculator = new ScoreCalculator(properties);

        DeterministicMatchResult result = new DeterministicMatchResult(
                80,  // requiredSkillScore
                90,  // experienceScore
                70,  // projectScore
                100, // educationScore
                50,  // keywordCoverage
                List.of(), List.of()
        );
        int aiQualityScore = 80;
        
        // Calculation: 
        // 80 * 0.35 = 28
        // 90 * 0.20 = 18
        // 70 * 0.15 = 10.5
        // 100 * 0.10 = 10
        // 50 * 0.10 = 5
        // 80 * 0.10 = 8
        // Total = 28 + 18 + 10.5 + 10 + 5 + 8 = 79.5 -> rounds to 80

        // Act
        AnalysisScoreDto scoreDto = scoreCalculator.calculateOverallScore(result, aiQualityScore);

        // Assert
        assertEquals(80, scoreDto.overallScore());
        assertEquals(80, scoreDto.skillsScore());
        assertEquals(70, scoreDto.projectScore());
        assertEquals(90, scoreDto.experienceScore());
        assertEquals(100, scoreDto.educationScore());
        assertEquals(50, scoreDto.keywordCoverage());
        assertEquals(80, scoreDto.aiQualityScore());
    }
}
