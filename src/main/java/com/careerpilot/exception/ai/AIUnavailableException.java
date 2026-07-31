package com.careerpilot.exception.ai;

public class AIUnavailableException extends RuntimeException {
    public AIUnavailableException(String message) {
        super(message);
    }

    public AIUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
