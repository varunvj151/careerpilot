package com.careerpilot.service;

import com.careerpilot.ai.BulletImprovementAiResult;
import com.careerpilot.ai.core.AIOrchestrator;
import com.careerpilot.dto.improvement.BulletImprovementDto;
import com.careerpilot.service.ImprovementRuleEngineService.WeakBullet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulletImprovementAiService {

    private final AIOrchestrator aiOrchestrator;

    /**
     * Processes a list of weak bullets through AI to generate improvements.
     */
    public List<BulletImprovementDto> generateImprovements(List<WeakBullet> weakBullets, String jobDescription) {
        List<BulletImprovementDto> improvements = new ArrayList<>();

        for (WeakBullet weakBullet : weakBullets) {
            try {
                log.info("Improving bullet {}: {}", weakBullet.bullet().id(), weakBullet.reason());
                
                BulletImprovementAiResult aiResult = aiOrchestrator.improveBullet(
                        weakBullet.bullet().originalText(),
                        weakBullet.reason(),
                        weakBullet.category(),
                        jobDescription
                );

                improvements.add(new BulletImprovementDto(
                        weakBullet.bullet().id(),
                        weakBullet.bullet().originalText(),
                        aiResult.improvedBullet(),
                        weakBullet.reason(),
                        weakBullet.category(),
                        aiResult.expectedAtsImpact(),
                        aiResult.confidence()
                ));
            } catch (Exception e) {
                log.error("Failed to generate improvement for bullet {}. Error: {}", weakBullet.bullet().id(), e.getMessage());
                // In a robust implementation, we might retry or just skip. 
                // We'll skip failed ones to ensure the overall process completes.
            }
        }

        return improvements;
    }
}
