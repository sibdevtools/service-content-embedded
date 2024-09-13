package com.github.simplemocks.content.embedded.conf;


import com.github.simplemocks.content.embedded.EnableContentServiceEmbedded;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

/**
 * Embedded content service condition.
 *
 * @author sibmaks
 * @since 0.0.5
 */
public class ContentServiceEmbeddedCondition implements Condition {
    @Override
    public boolean matches(@Nonnull ConditionContext context,
                           @Nonnull AnnotatedTypeMetadata metadata) {
        var beanFactory = Objects.requireNonNull(context.getBeanFactory());
        return beanFactory
                .getBeanNamesForAnnotation(EnableContentServiceEmbedded.class).length > 0;

    }
}
