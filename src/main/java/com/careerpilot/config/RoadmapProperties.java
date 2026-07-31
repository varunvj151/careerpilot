package com.careerpilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "roadmap")
public class RoadmapProperties {

    private Map<String, Integer> categoryPriorityWeights;
    private Map<String, Integer> defaultLearningHours;

    public Map<String, Integer> getCategoryPriorityWeights() {
        return categoryPriorityWeights;
    }

    public void setCategoryPriorityWeights(Map<String, Integer> categoryPriorityWeights) {
        this.categoryPriorityWeights = categoryPriorityWeights;
    }

    public Map<String, Integer> getDefaultLearningHours() {
        return defaultLearningHours;
    }

    public void setDefaultLearningHours(Map<String, Integer> defaultLearningHours) {
        this.defaultLearningHours = defaultLearningHours;
    }
}
