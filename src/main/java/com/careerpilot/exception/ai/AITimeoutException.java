package com.careerpilot.exception.ai;

public class AITimeoutException extends RuntimeException {
    public AITimeoutException(String message) {
        super(message);
    }

    public AITimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
