package com.github.sibdevtools.content.embedded.service;

import com.github.sibdevtools.content.api.condition.*;
import com.github.sibdevtools.content.api.dto.ContentHolder;
import com.github.sibdevtools.content.api.rq.GetContentRq;
import com.github.sibdevtools.content.api.rs.GetContentRs;
import com.github.sibdevtools.content.api.service.ContentService;
import com.github.sibdevtools.content.embedded.codec.ContentCodec;
import com.github.sibdevtools.content.embedded.entity.AttributeEntity;
import com.github.sibdevtools.content.embedded.exception.NotFoundException;
import com.github.sibdevtools.content.embedded.exception.NotSupportedException;
import com.github.sibdevtools.content.embedded.repository.AttributeRepository;
import com.github.sibdevtools.content.embedded.repository.ContentGroupRepository;
import com.github.sibdevtools.content.embedded.repository.ContentRepository;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "service.content.mode", havingValue = "EMBEDDED")
public class ContentServiceEmbedded implements ContentService {
    private final AttributeRepository attributeRepository;
    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final ContentCodec codec;

    @Override
    public <T> GetContentRs<T> getContent(@Nonnull GetContentRq<T> rq) {
        var contentGroup = contentGroupRepository.findBySystem_CodeAndTypeAndCode(
                rq.systemCode(),
                rq.type(),
                rq.groupCode()
        ).orElseThrow(() -> new NotFoundException("Content group not found"));

        var conditions = rq.conditions();

        var rsContents = new HashMap<String, ContentHolder<T>>();

        var contents = contentRepository.findAllByGroup(contentGroup);
        for (var content : contents) {
            var attributes = attributeRepository.findAllByContentId(content.getId())
                    .stream()
                    .filter(it -> it.getValue() != null)
                    .collect(Collectors.toMap(AttributeEntity::getCode, AttributeEntity::getValue));

            var allMet = conditions == null || checkConditions(conditions, attributes);

            if (!allMet) {
                continue;
            }
            var decoded = codec.decode(content.getContent(), rq.contentType());

            var contentCode = content.getCode();

            var contentHolder = ContentHolder.<T>builder()
                    .code(contentCode)
                    .content(decoded)
                    .attributes(attributes)
                    .build();

            rsContents.put(contentCode, contentHolder);
        }

        return new GetContentRs<>(rsContents);
    }

    private boolean checkConditions(List<Condition> conditions, Map<String, String> attributes) {
        for (var condition : conditions) {
            switch (condition) {
                case EqualsCondition equalsCondition -> {
                    var value = attributes.get(equalsCondition.getAttribute());
                    if (!Objects.equals(value, equalsCondition.getValue())) {
                        return false;
                    }
                }
                case NotEqualsCondition notEqualsCondition -> {
                    var value = attributes.get(notEqualsCondition.getAttribute());
                    if (Objects.equals(value, notEqualsCondition.getValue())) {
                        return false;
                    }
                }
                case IsNullCondition isNullCondition -> {
                    var value = attributes.get(isNullCondition.getAttribute());
                    if (value != null) {
                        return false;
                    }
                }
                case NotNullCondition notNullCondition -> {
                    var value = attributes.get(notNullCondition.getAttribute());
                    if (value == null) {
                        return false;
                    }
                }
                default -> throw new NotSupportedException("Unknown condition type");
            }
        }
        return true;
    }

}
