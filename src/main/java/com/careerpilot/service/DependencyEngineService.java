package com.careerpilot.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DependencyEngineService {

    private static final Map<String, Integer> PHASE_ORDER = Map.of(
            "Programming Language", 1,
            "Database", 1,
            "Framework", 2,
            "Cloud", 3,
            "DevOps", 3,
            "Other", 4
    );

    public int determinePhaseOrder(String classification) {
        return PHASE_ORDER.getOrDefault(classification, 4);
    }
    
    public String determineDependency(String classification) {
        if (classification.equals("Framework")) return "Core Programming Language";
        if (classification.equals("DevOps") || classification.equals("Cloud")) return "Frameworks and DBs";
        return "None";
    }
}
