package com.github.simple_mocks.content.embedded.constant;

import com.github.simple_mocks.error_service.api.ErrorSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constant {

    public static final ErrorSource ERROR_SOURCE = new ErrorSource("CONTENT_SERVICE");

}
