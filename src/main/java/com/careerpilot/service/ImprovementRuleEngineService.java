package com.careerpilot.service;

import com.careerpilot.dto.improvement.BulletDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ImprovementRuleEngineService {

    public record WeakBullet(
            BulletDto bullet,
            String reason,
            String category
    ) {}

    // Some simple deterministic patterns
    private static final Set<String> WEAK_VERBS = Set.of(
            "helped", "worked", "did", "made", "responsible for", "duties included", 
            "assisted", "handled", "managed to", "participated in", "contributed to"
    );

    private static final Pattern METRIC_PATTERN = Pattern.compile(".*\\b\\d+\\b.*|.*%.*|.*\\$|.*\\+.*");

    /**
     * Evaluates a list of bullets and returns the ones that need improvement.
     */
    public List<WeakBullet> evaluateBullets(List<BulletDto> bullets) {
        List<WeakBullet> weakBullets = new ArrayList<>();

        for (BulletDto bullet : bullets) {
            String text = bullet.originalText().trim().toLowerCase();

            // 1. Length check
            if (bullet.wordCount() < 5) {
                weakBullets.add(new WeakBullet(bullet, "Bullet is too short and lacks detail.", "Readability"));
                continue;
            }
            if (bullet.wordCount() > 40) {
                weakBullets.add(new WeakBullet(bullet, "Bullet is too long and may be hard to read.", "Readability"));
                continue;
            }

            // 2. Action Verbs Check (check if starts with weak verb or contains them prominently)
            boolean hasWeakVerb = WEAK_VERBS.stream().anyMatch(text::startsWith);
            if (hasWeakVerb) {
                weakBullets.add(new WeakBullet(bullet, "Starts with a weak or passive action verb.", "Action Verb"));
                continue;
            }

            // 3. Metrics Check
            if (!METRIC_PATTERN.matcher(text).matches()) {
                weakBullets.add(new WeakBullet(bullet, "Missing quantifiable metrics or results.", "Metrics"));
                continue;
            }

            // If it passes all basic deterministic checks, we can assume it's OK, 
            // or we could randomly sample it for "Impact" checks by AI. 
            // For now, only send genuinely weak bullets to AI to save cost.
        }

        return weakBullets;
    }
}
