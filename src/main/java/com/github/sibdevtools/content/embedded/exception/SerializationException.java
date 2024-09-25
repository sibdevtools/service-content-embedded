package com.github.sibdevtools.content.embedded.exception;

import com.github.sibdevtools.content.embedded.constant.Constant;
import com.github.sibdevtools.error.exception.ServiceException;
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
