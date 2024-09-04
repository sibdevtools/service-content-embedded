package com.github.simple_mocks.content.embedded.exception;

import com.github.simple_mocks.error_service.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.simple_mocks.content.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public class NotFoundException extends ServiceException {
    public NotFoundException(@Nonnull String systemMessage) {
        super(ERROR_SOURCE, "NOT_FOUND", systemMessage);
    }
}
