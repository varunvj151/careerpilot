package com.careerpilot.service;

import com.careerpilot.dto.roadmap.ProjectRecommendationDto;
import com.careerpilot.dto.roadmap.ResourceRecommendationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationEngineService {

    public List<ProjectRecommendationDto> recommendProjects(String skillName, String classification) {
        if (classification.equals("Framework")) {
            return List.of(new ProjectRecommendationDto("RESTful API with " + skillName, "Build a secure REST API handling CRUD operations using " + skillName));
        } else if (classification.equals("DevOps")) {
            return List.of(new ProjectRecommendationDto(skillName + " Pipeline", "Create a basic deployment or containerization pipeline using " + skillName));
        } else if (classification.equals("Database")) {
            return List.of(new ProjectRecommendationDto("Data Modeling with " + skillName, "Design and implement a relational or NoSQL schema for an e-commerce backend in " + skillName));
        }
        return List.of(new ProjectRecommendationDto("Fundamentals of " + skillName, "Create a small console or web app to demonstrate core features of " + skillName));
    }

    public List<ResourceRecommendationDto> recommendResources(String skillName) {
        return List.of(
                new ResourceRecommendationDto(skillName + " Official Documentation", "https://google.com/search?q=" + skillName + "+official+documentation", "Documentation"),
                new ResourceRecommendationDto(skillName + " Crash Course", "https://youtube.com/results?search_query=" + skillName + "+crash+course", "Video")
        );
    }
}
