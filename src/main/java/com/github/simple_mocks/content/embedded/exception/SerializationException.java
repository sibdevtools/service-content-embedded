package com.github.simple_mocks.content.embedded.exception;

import com.github.simple_mocks.error_service.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.simple_mocks.content.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class SerializationException extends ServiceException {
    public SerializationException(@Nonnull String systemMessage, @Nonnull Throwable cause) {
        super(ERROR_SOURCE, "SERIALIZATION_EXCEPTION", systemMessage, cause);
    }
}
