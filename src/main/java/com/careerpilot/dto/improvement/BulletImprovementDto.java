package com.careerpilot.dto.improvement;

import java.util.UUID;

public record BulletImprovementDto(
        UUID bulletId,
        String originalBullet,
        String improvedBullet,
        String reason,
        String category,
        String expectedAtsImpact,
        int confidence
) {}
