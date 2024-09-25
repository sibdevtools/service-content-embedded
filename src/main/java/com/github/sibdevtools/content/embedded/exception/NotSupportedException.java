package com.github.sibdevtools.content.embedded.exception;

import com.github.sibdevtools.error.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.sibdevtools.content.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class NotSupportedException extends ServiceException {
    public NotSupportedException(@Nonnull String systemMessage) {
        super(ERROR_SOURCE, "NOT_SUPPORTED", systemMessage);
    }
}
