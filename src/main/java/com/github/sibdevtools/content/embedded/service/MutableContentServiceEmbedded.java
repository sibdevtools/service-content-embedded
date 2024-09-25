package com.github.sibdevtools.content.embedded.service;

import com.github.sibdevtools.content.embedded.codec.ContentCodec;
import com.github.sibdevtools.content.embedded.entity.AttributeEntity;
import com.github.sibdevtools.content.embedded.entity.ContentEntity;
import com.github.sibdevtools.content.embedded.exception.NotFoundException;
import com.github.sibdevtools.content.embedded.repository.AttributeRepository;
import com.github.sibdevtools.content.embedded.repository.ContentGroupRepository;
import com.github.sibdevtools.content.embedded.repository.ContentRepository;
import com.github.sibdevtools.content.embedded.repository.SystemRepository;
import com.github.sibdevtools.content.mutable.api.rq.*;
import com.github.sibdevtools.content.mutable.api.service.MutableContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.5
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "service.content.mode", havingValue = "EMBEDDED")
public class MutableContentServiceEmbedded implements MutableContentService {
    private final AttributeRepository attributeRepository;
    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final SystemRepository systemRepository;
    private final ContentCodec codec;

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
