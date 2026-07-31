package com.careerpilot.ai.core;

import com.careerpilot.exception.ai.InvalidAIResponseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonMapperTest {

    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapper(new ObjectMapper());
    }

    record TestDto(String name, int age) {}

    @Test
    void testFromJson_Success() {
        String json = "{\"name\":\"John\", \"age\":30}";
        TestDto dto = jsonMapper.fromJson(json, TestDto.class);
        assertEquals("John", dto.name());
        assertEquals(30, dto.age());
    }

    @Test
    void testFromJson_MalformedJson() {
        String json = "{\"name\":\"John\", \"age\":30"; // Missing closing brace
        assertThrows(InvalidAIResponseException.class, () -> jsonMapper.fromJson(json, TestDto.class));
    }
}
