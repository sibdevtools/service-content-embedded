package com.github.simple_mocks.content.embedded.service;

import com.github.simple_mocks.content.api.condition.*;
import com.github.simple_mocks.content.api.dto.ContentHolder;
import com.github.simple_mocks.content.api.rq.GetContentRq;
import com.github.simple_mocks.content.api.rs.GetContentRs;
import com.github.simple_mocks.content.api.service.ContentService;
import com.github.simple_mocks.content.embedded.codec.ContentCodec;
import com.github.simple_mocks.content.embedded.entity.AttributeEntity;
import com.github.simple_mocks.content.embedded.entity.ContentEntity;
import com.github.simple_mocks.content.embedded.exception.NotFoundException;
import com.github.simple_mocks.content.embedded.exception.NotSupportedException;
import com.github.simple_mocks.content.embedded.repository.AttributeRepository;
import com.github.simple_mocks.content.embedded.repository.ContentGroupRepository;
import com.github.simple_mocks.content.embedded.repository.ContentRepository;
import com.github.simple_mocks.content.embedded.repository.SystemRepository;
import com.github.simple_mocks.content.mutable.api.rq.*;
import com.github.simple_mocks.content.mutable.api.service.MutableContentService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@RequiredArgsConstructor
public class ContentServiceEmbedded implements ContentService, MutableContentService {
    private final AttributeRepository attributeRepository;
    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final SystemRepository systemRepository;
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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSystem(CreateSystemRq rq) {
        systemRepository.saveIfNotExists(rq.systemCode());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteSystem(DeleteSystemRq rq) {
        systemRepository.deleteByCode(
                rq.systemCode()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createContentGroup(CreateContentGroupRq rq) {
        var systemCode = rq.systemCode();
        var systemEntity = systemRepository.findByCode(systemCode)
                .orElseThrow(() -> new NotFoundException("System not found"));

        contentGroupRepository.saveIfNotExists(
                systemEntity.getId(),
                rq.type(),
                rq.code()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteContentGroup(DeleteContentGroupRq rq) {
        contentGroupRepository.deleteBySystem_CodeAndTypeAndCode(
                rq.systemCode(),
                rq.type(),
                rq.groupCode()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void createContent(CreateContentRq<T> rq) {
        var optionalContentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCodeAndLock(
                rq.systemCode(),
                rq.type(),
                rq.groupCode(),
                rq.code()
        );

        if (optionalContentEntity.isPresent()) {
            return;
        }

        var contentGroup = contentGroupRepository.findBySystem_CodeAndTypeAndCode(
                rq.systemCode(),
                rq.type(),
                rq.groupCode()
        ).orElseThrow(() -> new NotFoundException("Content group not found"));

        var encoded = codec.encode(rq.content());

        var contentEntity = contentRepository.saveAndFlush(
                ContentEntity.builder()
                        .code(rq.code())
                        .content(encoded)
                        .group(contentGroup)
                        .createdAt(ZonedDateTime.now())
                        .modifiedAt(ZonedDateTime.now())
                        .build()
        );
        var contentEntityId = contentEntity.getId();

        var attributeEntities = rq.attributes()
                .entrySet()
                .stream()
                .map(it -> AttributeEntity.builder()
                        .contentId(contentEntityId)
                        .code(it.getKey())
                        .value(it.getValue())
                        .createdAt(ZonedDateTime.now())
                        .modifiedAt(ZonedDateTime.now())
                        .build())
                .toList();

        attributeRepository.saveAll(attributeEntities);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> void updateContent(UpdateContentRq<T> rq) {
        var contentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCodeAndLock(
                rq.systemCode(),
                rq.type(),
                rq.groupCode(),
                rq.code()
        ).orElseThrow(() -> new NotFoundException("Content not found"));

        var content = rq.content();
        var encodedContent = codec.encode(content);

        if (Objects.equals(encodedContent, contentEntity.getContent())) {
            return;
        }
        contentEntity.setContent(encodedContent);
        contentEntity.setModifiedAt(ZonedDateTime.now());
        contentRepository.save(contentEntity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateContentAttributes(UpdateContentAttributesRq rq) {
        var contentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCodeAndLock(
                rq.systemCode(),
                rq.type(),
                rq.groupCode(),
                rq.code()
        ).orElseThrow(() -> new NotFoundException("Content not found"));

        contentEntity.setModifiedAt(ZonedDateTime.now());
        contentRepository.save(contentEntity);

        var toRemove = new ArrayList<Long>();
        var toSave = new ArrayList<AttributeEntity>();

        var contentId = contentEntity.getId();
        var attributeEntities = attributeRepository.findAllByContentId(contentId);
        var attributes = new HashMap<>(rq.attributes());
        for (var attributeEntity : attributeEntities) {
            var attributeCode = attributeEntity.getCode();

            if (attributes.containsKey(attributeCode)) {
                var value = attributeEntity.getValue();
                var newValue = attributes.remove(attributeCode);
                if (!Objects.equals(value, newValue)) {
                    attributeEntity.setValue(newValue);
                    attributeEntity.setModifiedAt(ZonedDateTime.now());
                    toSave.add(attributeEntity);
                }
            } else {
                toRemove.add(attributeEntity.getId());
            }
        }

        for (var attributeEntry : attributes.entrySet()) {
            var attributeEntity = AttributeEntity.builder()
                    .contentId(contentId)
                    .code(attributeEntry.getKey())
                    .value(attributeEntry.getValue())
                    .createdAt(ZonedDateTime.now())
                    .modifiedAt(ZonedDateTime.now())
                    .build();
            toSave.add(attributeEntity);
        }

        attributeRepository.deleteAllById(toRemove);
        attributeRepository.saveAll(toSave);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteContent(DeleteContentRq rq) {
        var optionalContentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                rq.systemCode(),
                rq.type(),
                rq.groupCode(),
                rq.code()
        );
        if (optionalContentEntity.isEmpty()) {
            return;
        }
        var contentEntity = optionalContentEntity.get();

        attributeRepository.deleteAllByContentId(contentEntity.getId());
        contentRepository.deleteById(contentEntity.getId());
    }
}
