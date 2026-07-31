package com.careerpilot.service;

import com.careerpilot.config.RoadmapProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PriorityEngineService {

    private final RoadmapProperties roadmapProperties;

    public int calculatePriority(String classification, boolean isRequired) {
        int baseScore = isRequired ? 80 : 40;
        
        Map<String, Integer> categoryWeights = roadmapProperties.getCategoryPriorityWeights();
        if (categoryWeights != null && categoryWeights.containsKey(classification)) {
            baseScore += categoryWeights.get(classification);
        }

        return Math.min(100, baseScore);
    }
}
