package com.github.simplemocks.content.embedded;

import com.github.simplemocks.content.embedded.conf.ContentServiceEmbeddedConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enabler for content service embedded implementation.
 * <p>
 * Required H2 DataSource in context.
 * </p>
 *
 * @author sibmaks
 * @since 0.0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ContentServiceEmbeddedConfig.class)
public @interface EnableContentServiceEmbedded {
}
