package com.careerpilot.exception.ai;

public class PromptGenerationException extends RuntimeException {
    public PromptGenerationException(String message) {
        super(message);
    }

    public PromptGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
