package com.careerpilot.service;

import com.careerpilot.dto.analysis.ParsedJobDescriptionDto;
import com.careerpilot.dto.analysis.ParsedResumeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeterministicAnalysisService {

    public record DeterministicMatchResult(
            int requiredSkillScore,
            int experienceScore,
            int projectScore,
            int educationScore,
            int keywordCoverage,
            List<String> matchingSkills,
            List<String> missingSkills
    ) {}

    public DeterministicMatchResult analyze(ParsedResumeDto resume, ParsedJobDescriptionDto jd) {
        log.info("Starting deterministic analysis");

        List<String> matchingSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        int requiredSkillScore = calculateSkillCoverage(resume.skills(), jd.requiredSkills(), matchingSkills, missingSkills);
        int experienceScore = calculateExperienceMatch(resume.experience(), jd.minimumExperience());
        int projectScore = calculateProjectMatch(resume.projects(), jd.technologies());
        int educationScore = calculateEducationMatch(resume.education(), jd.educationRequirements());
        int keywordCoverage = calculateKeywordCoverage(resume.summary(), resume.experience(), jd.responsibilities());

        return new DeterministicMatchResult(
                requiredSkillScore,
                experienceScore,
                projectScore,
                educationScore,
                keywordCoverage,
                matchingSkills,
                missingSkills
        );
    }

    private int calculateSkillCoverage(List<String> resumeSkills, List<String> requiredSkills,
                                       List<String> matchingSkillsOut, List<String> missingSkillsOut) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 100;
        
        List<String> normalizedResumeSkills = resumeSkills == null ? List.of() : 
                resumeSkills.stream().map(String::toLowerCase).collect(Collectors.toList());

        int matchCount = 0;
        for (String req : requiredSkills) {
            String nReq = req.toLowerCase();
            boolean matched = normalizedResumeSkills.stream().anyMatch(s -> s.contains(nReq) || nReq.contains(s));
            if (matched) {
                matchCount++;
                matchingSkillsOut.add(req);
            } else {
                missingSkillsOut.add(req);
            }
        }
        return (int) Math.round(((double) matchCount / requiredSkills.size()) * 100);
    }

    private int calculateExperienceMatch(List<String> experienceList, String minimumExperience) {
        if (minimumExperience == null || minimumExperience.isBlank()) return 100;
        if (experienceList == null || experienceList.isEmpty()) return 0;
        
        // Simple heuristic: count items in experience list as years.
        int yearsOfExperience = experienceList.size(); 
        
        int reqYears = 0;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(minimumExperience);
        if (m.find()) {
            reqYears = Integer.parseInt(m.group(1));
        }

        if (reqYears == 0) return 100;
        if (yearsOfExperience >= reqYears) return 100;
        
        return (int) Math.round(((double) yearsOfExperience / reqYears) * 100);
    }

    private int calculateProjectMatch(List<String> projects, List<String> technologies) {
        if (technologies == null || technologies.isEmpty()) return 100;
        if (projects == null || projects.isEmpty()) return 0;

        String allProjectsText = String.join(" ", projects).toLowerCase();
        int techCount = 0;
        for (String tech : technologies) {
            if (allProjectsText.contains(tech.toLowerCase())) {
                techCount++;
            }
        }
        
        return (int) Math.round(((double) techCount / technologies.size()) * 100);
    }

    private int calculateEducationMatch(List<String> resumeEducation, List<String> reqEducation) {
        if (reqEducation == null || reqEducation.isEmpty()) return 100;
        if (resumeEducation == null || resumeEducation.isEmpty()) return 0;

        String eduText = String.join(" ", resumeEducation).toLowerCase();
        for (String req : reqEducation) {
            // Check for basic degrees
            if (req.toLowerCase().contains("bachelor") && eduText.contains("bachelor")) return 100;
            if (req.toLowerCase().contains("master") && eduText.contains("master")) return 100;
            if (req.toLowerCase().contains("bs") && eduText.contains("bs")) return 100;
        }
        // Fallback score
        return 50; 
    }

    private int calculateKeywordCoverage(String summary, List<String> experience, List<String> responsibilities) {
        if (responsibilities == null || responsibilities.isEmpty()) return 100;
        
        String fullText = (summary != null ? summary : "") + " " + 
                          (experience != null ? String.join(" ", experience) : "");
        fullText = fullText.toLowerCase();

        int hitCount = 0;
        for (String resp : responsibilities) {
            // Extract important nouns/verbs from responsibilities
            String[] words = resp.toLowerCase().split("\\W+");
            boolean found = false;
            for (String word : words) {
                if (word.length() > 4 && fullText.contains(word)) { // Only check words > 4 chars
                    found = true;
                    break;
                }
            }
            if (found) hitCount++;
        }
        
        return (int) Math.round(((double) hitCount / responsibilities.size()) * 100);
    }
}
