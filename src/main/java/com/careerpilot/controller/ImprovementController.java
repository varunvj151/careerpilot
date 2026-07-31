package com.careerpilot.controller;

import com.careerpilot.dto.response.Responses.ImprovementResponse;
import com.careerpilot.service.ImprovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/improvement")
@RequiredArgsConstructor
public class ImprovementController {

    private final ImprovementService improvementService;

    @PostMapping("/{analysisId}")
    public ResponseEntity<com.careerpilot.dto.response.Responses.JobResponse> generateImprovement(@PathVariable UUID analysisId) {
        com.careerpilot.dto.response.Responses.JobResponse response = improvementService.createImprovementJob(analysisId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<ImprovementResponse> getImprovement(@PathVariable UUID analysisId) {
        ImprovementResponse response = improvementService.getImprovement(analysisId);
        return ResponseEntity.ok(response);
    }
}
