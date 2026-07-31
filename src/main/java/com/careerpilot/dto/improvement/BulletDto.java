package com.careerpilot.dto.improvement;

import java.util.UUID;

public record BulletDto(
        UUID id,
        String section,
        String originalText,
        int position,
        int wordCount
) {}
