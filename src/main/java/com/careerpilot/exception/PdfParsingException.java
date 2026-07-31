package com.careerpilot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PdfParsingException extends RuntimeException {
    public PdfParsingException(String message) {
        super(message);
    }

    public PdfParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
