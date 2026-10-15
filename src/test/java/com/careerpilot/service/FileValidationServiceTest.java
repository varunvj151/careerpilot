package com.careerpilot.service;

import com.careerpilot.exception.InvalidFileException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        fileValidationService = new FileValidationService();
    }

    @Test
    void validateFile_ThrowsException_WhenFileIsNull() {
        assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(null));
    }

    @Test
    void validateFile_ThrowsException_WhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf", new byte[0]
        );
        assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(emptyFile));
    }

    @Test
    void validateFile_ThrowsException_WhenNonPdf() {
        MockMultipartFile textFile = new MockMultipartFile(
                "file", "resume.txt", "text/plain", "Sample content".getBytes()
        );
        assertThrows(InvalidFileException.class, () -> fileValidationService.validateFile(textFile));
    }

    @Test
    void validateFile_Success_WithValidPdf() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.save(baos);
        }

        MockMultipartFile pdfFile = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", baos.toByteArray()
        );

        assertDoesNotThrow(() -> fileValidationService.validateFile(pdfFile));
    }
}
