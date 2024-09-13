package com.github.simplemocks.content.embedded.exception;

import com.github.simplemocks.error_service.exception.ServiceException;
import com.github.simplemocks.content.embedded.constant.Constant;
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
