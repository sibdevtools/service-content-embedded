package com.github.sibdevtools.content.embedded.exception;

import com.github.sibdevtools.content.embedded.constant.Constant;
import com.github.sibdevtools.error.exception.ServiceException;
import jakarta.annotation.Nonnull;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class NotFoundException extends ServiceException {
    public NotFoundException(@Nonnull String systemMessage) {
        super(Constant.ERROR_SOURCE, "NOT_FOUND", systemMessage);
    }
}
