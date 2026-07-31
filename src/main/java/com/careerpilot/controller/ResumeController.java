package com.careerpilot.controller;

import com.careerpilot.dto.response.Responses.ResumeResponse;
import com.careerpilot.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<ResumeResponse> uploadResume(@RequestParam("file") MultipartFile file) {
        ResumeResponse response = resumeService.uploadResume(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getAllResumes() {
        return ResponseEntity.ok(resumeService.getAllResumes());
    }
}
