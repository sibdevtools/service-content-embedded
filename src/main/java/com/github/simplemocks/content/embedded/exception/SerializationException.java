package com.github.simplemocks.content.embedded.exception;

import com.github.simplemocks.error_service.exception.ServiceException;
import com.github.simplemocks.content.embedded.constant.Constant;
import jakarta.annotation.Nonnull;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class SerializationException extends ServiceException {
    public SerializationException(@Nonnull String systemMessage, @Nonnull Throwable cause) {
        super(Constant.ERROR_SOURCE, "SERIALIZATION_EXCEPTION", systemMessage, cause);
    }
}
