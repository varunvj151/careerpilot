package com.careerpilot.ai.prompt;

public interface PromptBuilder<T> {
    String buildPrompt(T request);
}
