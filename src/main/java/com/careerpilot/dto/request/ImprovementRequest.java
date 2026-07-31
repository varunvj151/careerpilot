package com.careerpilot.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImprovementRequest(
        @NotNull(message = "Analysis ID is required")
        UUID analysisId
) {}
