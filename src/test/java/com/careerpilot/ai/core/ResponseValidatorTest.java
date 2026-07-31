package com.careerpilot.ai.core;

import com.careerpilot.exception.ai.InvalidAIResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponseValidatorTest {

    private ResponseValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ResponseValidator();
    }

    @Test
    void testCleanAndValidate_Success() {
        String raw = "```json\n{\"test\":\"value\"}\n```";
        String result = validator.cleanAndValidate(raw);
        assertEquals("{\"test\":\"value\"}", result);
    }

    @Test
    void testCleanAndValidate_EmptyResponse() {
        assertThrows(InvalidAIResponseException.class, () -> validator.cleanAndValidate("   "));
    }

    @Test
    void testCleanAndValidate_NotJson() {
        assertThrows(InvalidAIResponseException.class, () -> validator.cleanAndValidate("This is just some text."));
    }
}
