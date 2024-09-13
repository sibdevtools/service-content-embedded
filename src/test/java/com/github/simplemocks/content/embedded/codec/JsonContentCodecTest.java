package com.github.simplemocks.content.embedded.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ExtendWith(MockitoExtension.class)
class JsonContentCodecTest {
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private JsonContentCodec codec;

    @Test
    void testEncode() throws JsonProcessingException {
        var content = mock(Object.class);
        var encoded = UUID.randomUUID().toString();
        when(objectMapper.writeValueAsString(content))
                .thenReturn(encoded);

        var actual = codec.encode(content);
        assertEquals(encoded, actual);
    }

    @Test
    void testDecode() throws JsonProcessingException {
        var decoded = mock(Object.class);
        var encoded = UUID.randomUUID().toString();
        var type = Object.class;
        when(objectMapper.readValue(encoded, type))
                .thenReturn(decoded);

        var actual = codec.decode(encoded, type);
        assertEquals(decoded, actual);
    }

    @Test
    void testDecodeNull() {
        var actual = codec.decode(null, null);
        assertNull(actual);
    }

}