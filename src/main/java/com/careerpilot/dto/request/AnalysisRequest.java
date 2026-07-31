package com.careerpilot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AnalysisRequest(

        @NotNull(message = "Resume ID is required")
        UUID resumeId,

        @NotBlank(message = "Job description text is required")
        @Size(min = 100, max = 20000, message = "Job description must be between 100 and 20000 characters")
        String jobDescriptionText,

        @Size(max = 255, message = "Job title must not exceed 255 characters")
        String jobTitle,

        @Size(max = 255, message = "Company name must not exceed 255 characters")
        String company
) {}
