package com.careerpilot.controller;

import com.careerpilot.dto.request.AnalysisRequest;
import com.careerpilot.dto.response.Responses.AnalysisResponse;
import com.careerpilot.dto.response.Responses.JobResponse;
import com.careerpilot.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @PostMapping
    public ResponseEntity<JobResponse> analyzeResume(@Valid @RequestBody AnalysisRequest request) {
        JobResponse response = analysisService.createAnalysisJob(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponse> getAnalysis(@PathVariable UUID id) {
        AnalysisResponse response = analysisService.getAnalysis(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<java.util.List<AnalysisResponse>> getAnalysisHistory() {
        return ResponseEntity.ok(analysisService.getAnalysisHistory());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable UUID id) {
        analysisService.deleteAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
