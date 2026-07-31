package com.careerpilot.service;

import com.careerpilot.dto.response.Responses.ResumeResponse;
import com.careerpilot.entity.Resume;
import com.careerpilot.entity.User;
import com.careerpilot.exception.PdfParsingException;
import com.careerpilot.repository.ResumeRepository;
import com.careerpilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final PdfValidationService pdfValidationService;

    @Transactional
    public ResumeResponse uploadResume(MultipartFile file) {
        long start = System.currentTimeMillis();
        
        pdfValidationService.validatePdf(file);

        String rawText = extractTextFromPdf(file);
        
        long end = System.currentTimeMillis();
        log.info("PDF validation and extraction took {} ms for file {}", (end - start), file.getOriginalFilename());

        User currentUser = getCurrentUser();

        Resume resume = Resume.builder()
                .user(currentUser)
                .fileName(file.getOriginalFilename())
                .rawText(rawText)
                .build();

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeResponse(
                savedResume.getId(),
                savedResume.getFileName(),
                savedResume.getUploadedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> getAllResumes() {
        User currentUser = getCurrentUser();
        return resumeRepository.findByUserIdOrderByUploadedAtDesc(currentUser.getId()).stream()
                .map(r -> new ResumeResponse(r.getId(), r.getFileName(), r.getUploadedAt()))
                .collect(Collectors.toList());
    }

    private String extractTextFromPdf(MultipartFile file) {
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(
                new org.apache.pdfbox.io.RandomAccessReadBuffer(file.getInputStream()))) {
            PDFTextStripper stripper = new PDFTextStripper();
            // Preserve basic formatting
            stripper.setSortByPosition(true);
            String rawText = stripper.getText(document);
            return cleanText(rawText);
        } catch (IOException e) {
            log.error("Failed to parse PDF during extraction", e);
            throw new PdfParsingException("Failed to extract text from PDF.", e);
        }
    }

    private String cleanText(String text) {
        if (text == null) return "";
        // Keep printable ASCII, newlines, and tabs. Remove other invalid characters
        text = text.replaceAll("[^\\x20-\\x7E\\n\\r\\t]", ""); 
        // Remove duplicate spaces
        text = text.replaceAll(" +", " ");
        // Remove empty lines or lines with just whitespace
        text = text.replaceAll("(?m)^[ \\t]*\\r?\\n", "");
        return text.trim();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found in database"));
    }
}
