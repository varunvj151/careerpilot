package com.careerpilot.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.careerpilot.dto.analysis.ParsedResumeDto;
import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.AnalysisScoreDto;
import com.careerpilot.dto.analysis.MissingSkillDto;

@Entity
@Table(name = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_description_id", nullable = false)
    private JobDescription jobDescription;

    @Column(name = "match_percentage", precision = 5, scale = 2)
    private BigDecimal matchPercentage;

    @Type(JsonType.class)
    @Column(name = "matching_skills", columnDefinition = "json")
    private List<String> matchingSkills;

    @Type(JsonType.class)
    @Column(name = "missing_skills", columnDefinition = "json")
    private List<MissingSkillDto> missingSkills;

    @Type(JsonType.class)
    @Column(name = "strengths", columnDefinition = "json")
    private List<String> strengths;

    @Type(JsonType.class)
    @Column(name = "weaknesses", columnDefinition = "json")
    private List<String> weaknesses;

    @Type(JsonType.class)
    @Column(name = "extracted_resume", columnDefinition = "json")
    private ParsedResumeDto extractedResume;

    @Type(JsonType.class)
    @Column(name = "extracted_jd", columnDefinition = "json")
    private ParsedJobDescriptionDto extractedJd;

    @Type(JsonType.class)
    @Column(name = "scores", columnDefinition = "json")
    private AnalysisScoreDto scores;

    @Type(JsonType.class)
    @Column(name = "recommendations", columnDefinition = "json")
    private List<com.careerpilot.dto.analysis.RecommendationDto> recommendations;

    @Type(JsonType.class)
    @Column(name = "timing", columnDefinition = "json")
    private com.careerpilot.dto.analysis.AnalysisTimingDto timing;

    @Column(name = "ai_model", length = 100)
    private String aiModel;

    @Column(name = "analysis_version", length = 50)
    private String analysisVersion;

    @Column(name = "raw_ai_response", columnDefinition = "TEXT")
    private String rawAiResponse;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
