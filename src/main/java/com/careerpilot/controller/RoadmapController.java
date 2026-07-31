package com.careerpilot.controller;

import com.careerpilot.dto.response.Responses.RoadmapResponse;
import com.careerpilot.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @PostMapping("/{analysisId}")
    public ResponseEntity<com.careerpilot.dto.response.Responses.JobResponse> generateRoadmap(@PathVariable UUID analysisId) {
        com.careerpilot.dto.response.Responses.JobResponse response = roadmapService.createRoadmapJob(analysisId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<RoadmapResponse> getRoadmap(@PathVariable UUID analysisId) {
        RoadmapResponse response = roadmapService.getRoadmap(analysisId);
        return ResponseEntity.ok(response);
    }
}
