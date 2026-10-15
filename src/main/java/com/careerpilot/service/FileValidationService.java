package com.careerpilot.service;

import com.careerpilot.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("application/pdf");

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty or missing. Please upload a PDF resume.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                "Only PDF files are supported. Please convert your resume to PDF and try again. " +
                "Received: " + (contentType != null ? contentType : "unknown")
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File size exceeds the maximum limit of 5MB. Please compress your PDF and try again.");
        }

        // Validate PDF integrity with PDFBox
        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(
                new org.apache.pdfbox.io.RandomAccessReadBuffer(file.getInputStream()))) {
            if (document.isEncrypted()) {
                log.warn("Encrypted PDF uploaded: {}", file.getOriginalFilename());
                throw new InvalidFileException("Cannot parse an encrypted or password-protected PDF. Please remove the password and try again.");
            }
            int pageCount = document.getNumberOfPages();
            if (pageCount == 0) {
                throw new InvalidFileException("The uploaded PDF appears to be empty. Please check your file and try again.");
            }
            log.info("PDF validated successfully: {} pages, {}KB", pageCount, file.getSize() / 1024);
        } catch (InvalidFileException e) {
            throw e;
        } catch (org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException e) {
            log.warn("Encrypted PDF uploaded: {}", file.getOriginalFilename());
            throw new InvalidFileException("Cannot parse a password-protected PDF. Please remove the password and try again.", e);
        } catch (IOException e) {
            log.error("Failed to parse PDF during validation", e);
            throw new InvalidFileException("Failed to read the PDF file. It may be corrupted. Please try a different file.", e);
        }
    }
}

