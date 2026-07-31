package com.careerpilot.exception;

import com.careerpilot.dto.response.Responses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Responses.ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Responses.ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return error(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Responses.ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return error(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Responses.ErrorResponse> handleInvalidFile(InvalidFileException ex) {
        return error(HttpStatus.BAD_REQUEST, "Invalid File", ex.getMessage());
    }

    @ExceptionHandler(PdfParsingException.class)
    public ResponseEntity<Responses.ErrorResponse> handlePdfParsing(PdfParsingException ex) {
        return error(HttpStatus.BAD_REQUEST, "PDF Parsing Error", ex.getMessage());
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<Responses.ErrorResponse> handleAiService(AiServiceException ex) {
        log.error("AI service error: {}", ex.getMessage(), ex);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "AI Service Unavailable",
                "The AI service is temporarily unavailable. Please try again in a moment.");
    }

    @ExceptionHandler(com.careerpilot.exception.ai.AIUnavailableException.class)
    public ResponseEntity<Responses.ErrorResponse> handleAIUnavailable(com.careerpilot.exception.ai.AIUnavailableException ex) {
        log.error("AI service unavailable: {}", ex.getMessage(), ex);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "AI Unavailable", "The AI service is currently unavailable. Please try again later.");
    }

    @ExceptionHandler(com.careerpilot.exception.ai.AITimeoutException.class)
    public ResponseEntity<Responses.ErrorResponse> handleAITimeout(com.careerpilot.exception.ai.AITimeoutException ex) {
        log.error("AI timeout: {}", ex.getMessage(), ex);
        return error(HttpStatus.GATEWAY_TIMEOUT, "AI Timeout", "The AI service took too long to respond. Please try again later.");
    }

    @ExceptionHandler(com.careerpilot.exception.ai.InvalidAIResponseException.class)
    public ResponseEntity<Responses.ErrorResponse> handleInvalidAIResponse(com.careerpilot.exception.ai.InvalidAIResponseException ex) {
        log.error("Invalid AI response: {}", ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid AI Response", "The AI service returned an invalid response.");
    }

    @ExceptionHandler(com.careerpilot.exception.ai.PromptGenerationException.class)
    public ResponseEntity<Responses.ErrorResponse> handlePromptGeneration(com.careerpilot.exception.ai.PromptGenerationException ex) {
        log.error("Prompt generation failed: {}", ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "AI Request Error", "Failed to generate the AI request.");
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Responses.ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid email or password.");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Responses.ErrorResponse> handleAuthentication(AuthenticationException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication failed.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Responses.ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        List<Responses.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new Responses.FieldError(e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Responses.ValidationErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        fieldErrors,
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Responses.ErrorResponse> handleFileSizeExceeded(MaxUploadSizeExceededException ex) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "File Too Large",
                "File size exceeds the maximum allowed limit of 10MB.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Responses.ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<Responses.ErrorResponse> error(HttpStatus status, String error, String message) {
        return ResponseEntity
                .status(status)
                .body(new Responses.ErrorResponse(status.value(), error, message, LocalDateTime.now()));
    }
}
