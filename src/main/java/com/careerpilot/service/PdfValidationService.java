package com.careerpilot.service;

import com.careerpilot.exception.PdfParsingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class PdfValidationService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PdfParsingException("File is empty or missing");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new PdfParsingException("Only PDF files are supported");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new PdfParsingException("File size exceeds the maximum limit of 5MB");
        }

        // Try to open with PDFBox to detect encryption or corruption
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(
                new org.apache.pdfbox.io.RandomAccessReadBuffer(file.getInputStream()))) {
            if (document.isEncrypted()) {
                log.warn("Encrypted PDF uploaded: {}", file.getOriginalFilename());
                throw new PdfParsingException("Cannot parse an encrypted PDF file");
            }
        } catch (org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException e) {
            log.warn("Encrypted PDF uploaded (InvalidPasswordException): {}", file.getOriginalFilename());
            throw new PdfParsingException("Cannot parse an encrypted PDF file", e);
        } catch (IOException e) {
            log.error("Failed to parse PDF", e);
            throw new PdfParsingException("Failed to parse PDF file. It might be corrupted.", e);
        }
    }
}
