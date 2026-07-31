package com.careerpilot.service;

import com.careerpilot.config.RoadmapProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LearningTimeEstimatorService {

    private final RoadmapProperties roadmapProperties;

    public int estimateHours(String classification) {
        Map<String, Integer> defaultHours = roadmapProperties.getDefaultLearningHours();
        if (defaultHours != null && defaultHours.containsKey(classification)) {
            return defaultHours.get(classification);
        }
        return 40; // Default 40 hours
    }
}
