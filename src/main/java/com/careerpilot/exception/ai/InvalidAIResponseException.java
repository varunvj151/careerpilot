package com.careerpilot.exception.ai;

public class InvalidAIResponseException extends RuntimeException {
    public InvalidAIResponseException(String message) {
        super(message);
    }

    public InvalidAIResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
