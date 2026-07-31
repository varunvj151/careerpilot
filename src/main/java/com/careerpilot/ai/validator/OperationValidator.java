package com.careerpilot.ai.validator;

public interface OperationValidator<T> {
    void validate(T response);
}
