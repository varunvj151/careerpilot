package com.careerpilot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "score.weights")
public class ScoreProperties {
    private double requiredSkills = 0.35;
    private double experience = 0.20;
    private double projects = 0.15;
    private double education = 0.10;
    private double keywordCoverage = 0.10;
    private double aiQuality = 0.10;
}
