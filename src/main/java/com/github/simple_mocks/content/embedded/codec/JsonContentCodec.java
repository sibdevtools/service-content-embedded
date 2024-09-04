package com.github.simple_mocks.content.embedded.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.simple_mocks.content.embedded.exception.SerializationException;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Builder
@AllArgsConstructor
public class JsonContentCodec implements ContentCodec {
    private final ObjectMapper objectMapper;

    @Override
    public <T> String encode(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Can't encode content", e);
        }
    }

    @Override
    public <T> T decode(String value, Class<T> type) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Can't decode content", e);
        }
    }
}
