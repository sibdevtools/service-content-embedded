package com.github.sibdevtools.content.embedded;

import com.github.sibdevtools.content.embedded.entity.AttributeEntity;
import com.github.sibdevtools.content.embedded.entity.ContentEntity;
import com.github.sibdevtools.content.embedded.exception.NotFoundException;
import com.github.sibdevtools.content.embedded.repository.AttributeRepository;
import com.github.sibdevtools.content.embedded.repository.ContentGroupRepository;
import com.github.sibdevtools.content.embedded.repository.ContentRepository;
import com.github.sibdevtools.content.embedded.repository.SystemRepository;
import com.github.sibdevtools.content.mutable.api.rq.*;
import com.github.sibdevtools.content.mutable.api.service.MutableContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ActiveProfiles("startup-test")
@SpringBootTest
class MutableContentServiceIntegrationTest {
    @Autowired
    private MutableContentService mutableContentService;
    @Autowired
    private SystemRepository systemRepository;
    @Autowired
    private ContentGroupRepository contentGroupRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private AttributeRepository attributeRepository;

    @Test
    void testCreateSystemThenDelete() {
        var systemCode = UUID.randomUUID().toString();
        var createRq = CreateSystemRq.builder()
                .systemCode(systemCode)
                .build();
        mutableContentService.createSystem(createRq);
        mutableContentService.createSystem(createRq);

        var systemEntity = systemRepository.findByCode(systemCode);
        assertNotNull(systemEntity);
        assertTrue(systemEntity.isPresent());

        var deleteRq = DeleteSystemRq.builder()
                .systemCode(systemCode.toLowerCase())
                .build();
        mutableContentService.deleteSystem(deleteRq);

        systemEntity = systemRepository.findByCode(systemCode);
        assertNotNull(systemEntity);
        assertTrue(systemEntity.isEmpty());
    }

    @Test
    void testCreateContentGroupThenDelete() {
        var systemCode = UUID.randomUUID().toString();
        mutableContentService.createSystem(CreateSystemRq.builder()
                .systemCode(systemCode)
                .build());

        var groupType = UUID.randomUUID().toString();
        var groupCode = UUID.randomUUID().toString();

        var createRq = CreateContentGroupRq.builder()
                .systemCode(systemCode)
                .type(groupType)
                .code(groupCode)
                .build();
        mutableContentService.createContentGroup(createRq);
        mutableContentService.createContentGroup(createRq);

        var groupEntities = contentGroupRepository.findBySystem_CodeAndTypeAndCode(
                systemCode, groupType, groupCode
        );
        assertNotNull(groupEntities);
        assertTrue(groupEntities.isPresent());

        mutableContentService.deleteContentGroup(
                DeleteContentGroupRq.builder()
                        .systemCode(systemCode)
                        .type(groupType)
                        .groupCode(groupCode)
                        .build()
        );

        groupEntities = contentGroupRepository.findBySystem_CodeAndTypeAndCode(
                systemCode, groupType, groupCode
        );
        assertNotNull(groupEntities);
        assertTrue(groupEntities.isEmpty());
    }

    @Test
    void testCreateContentGroupWhenSystemDoesNotExist() {
        var rq = CreateContentGroupRq.builder()
                .systemCode(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                .code(UUID.randomUUID().toString())
                .build();

        var notFoundException = assertThrows(
                NotFoundException.class,
                () -> mutableContentService.createContentGroup(rq)
        );
        assertEquals("System not found", notFoundException.getMessage());
    }

    @Test
    void testCreateContentThenDelete() {
        var systemCode = UUID.randomUUID().toString();
        mutableContentService.createSystem(CreateSystemRq.builder()
                .systemCode(systemCode)
                .build());

        var groupType = UUID.randomUUID().toString();
        var groupCode = UUID.randomUUID().toString();
        mutableContentService.createContentGroup(
                CreateContentGroupRq.builder()
                        .systemCode(systemCode)
                        .type(groupType)
                        .code(groupCode)
                        .build()
        );

        var contentCode = UUID.randomUUID().toString();
        var content = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        var attributes = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var createRq = CreateContentRq.<Map<String, String>>builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .content(content)
                .attributes(attributes)
                .build();
        mutableContentService.createContent(createRq);
        mutableContentService.createContent(createRq);

        var contentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                systemCode, groupType, groupCode, contentCode
        );
        assertNotNull(contentEntity);
        assertTrue(contentEntity.isPresent());

        var deleteRq = DeleteContentRq.builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .build();
        mutableContentService.deleteContent(deleteRq);

        contentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                systemCode, groupType, groupCode, contentCode
        );
        assertNotNull(contentEntity);
        assertTrue(contentEntity.isEmpty());

        mutableContentService.deleteContent(deleteRq);
    }

    @Test
    void testCreateContentThenUpdateContent() {
        var systemCode = UUID.randomUUID().toString();
        mutableContentService.createSystem(CreateSystemRq.builder()
                .systemCode(systemCode)
                .build());

        var groupType = UUID.randomUUID().toString();
        var groupCode = UUID.randomUUID().toString();
        mutableContentService.createContentGroup(
                CreateContentGroupRq.builder()
                        .systemCode(systemCode)
                        .type(groupType)
                        .code(groupCode)
                        .build()
        );

        var contentCode = UUID.randomUUID().toString();
        var content = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        var attributes = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var createRq = CreateContentRq.<Map<String, String>>builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .content(content)
                .attributes(attributes)
                .build();
        mutableContentService.createContent(createRq);

        var oldContentEntity = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                        systemCode,
                        groupType,
                        groupCode,
                        contentCode
                )
                .orElse(null);
        assertNotNull(oldContentEntity);

        var newContent = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var updateRq = UpdateContentRq.builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .content(newContent)
                .build();
        mutableContentService.updateContent(updateRq);
        mutableContentService.updateContent(updateRq);

        var newContentEntity = contentRepository.findById(oldContentEntity.getId())
                .orElse(null);
        assertNotNull(newContentEntity);
        assertNotEquals(oldContentEntity.getContent(), newContentEntity.getContent());
    }

    @Test
    void testUpdateContentWhenContentDoesNotExist() {
        var content = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var rq = UpdateContentRq.builder()
                .systemCode(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                .groupCode(UUID.randomUUID().toString())
                .code(UUID.randomUUID().toString())
                .content(content)
                .build();

        var notFoundException = assertThrows(
                NotFoundException.class,
                () -> mutableContentService.updateContent(rq)
        );
        assertEquals("Content not found", notFoundException.getMessage());
    }

    @Test
    void testCreateContentWhenContentGroupDoesNotExist() {
        var content = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        var attributes = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        var rq = CreateContentRq.<Map<String, String>>builder()
                .systemCode(UUID.randomUUID().toString())
                .type(UUID.randomUUID().toString())
                .groupCode(UUID.randomUUID().toString())
                .code(UUID.randomUUID().toString())
                .content(content)
                .attributes(attributes)
                .build();

        var notFoundException = assertThrows(
                NotFoundException.class,
                () -> mutableContentService.createContent(rq)
        );
        assertEquals("Content group not found", notFoundException.getMessage());
    }

    @Test
    void testCreateContentThenUpdateContentAttributes() {
        var systemCode = UUID.randomUUID().toString();
        mutableContentService.createSystem(CreateSystemRq.builder()
                .systemCode(systemCode)
                .build());

        var groupType = UUID.randomUUID().toString();
        var groupCode = UUID.randomUUID().toString();
        mutableContentService.createContentGroup(
                CreateContentGroupRq.builder()
                        .systemCode(systemCode)
                        .type(groupType)
                        .code(groupCode)
                        .build()
        );

        var contentCode = UUID.randomUUID().toString();
        var content = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var notChangedKey = UUID.randomUUID().toString();
        var notChangedValue = UUID.randomUUID().toString();
        var toRemoveKey = UUID.randomUUID().toString();
        var toRemoveValue = UUID.randomUUID().toString();
        var toChangeKey = UUID.randomUUID().toString();
        var toChangeValue = UUID.randomUUID().toString();
        var attributes = Map.of(
                notChangedKey, notChangedValue,
                toRemoveKey, toRemoveValue,
                toChangeKey, toChangeValue
        );

        var createRq = CreateContentRq.<Map<String, String>>builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .content(content)
                .attributes(attributes)
                .build();
        mutableContentService.createContent(createRq);

        var contentId = contentRepository.findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                        systemCode, groupType, groupCode, contentCode
                )
                .map(ContentEntity::getId)
                .orElse(null);

        var toAddKey = UUID.randomUUID().toString();
        var toAddValue = UUID.randomUUID().toString();

        var toChangeNewValue = UUID.randomUUID().toString();

        var newAttributes = Map.of(
                notChangedKey, notChangedValue,
                toChangeKey, toChangeNewValue,
                toAddKey, toAddValue
        );

        var updateRq = UpdateContentAttributesRq.builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .attributes(newAttributes)
                .build();
        mutableContentService.updateContentAttributes(updateRq);
        mutableContentService.updateContentAttributes(updateRq);

        var attributeEntities = attributeRepository.findAllByContentId(contentId);
        assertEquals(3, attributeEntities.size());

        var attributesMap = attributeEntities.stream()
                .collect(Collectors.toMap(AttributeEntity::getCode, AttributeEntity::getValue));

        assertEquals(toAddValue, attributesMap.get(toAddKey));
        assertEquals(toChangeNewValue, attributesMap.get(toChangeKey));
        assertEquals(notChangedValue, attributesMap.get(notChangedKey));
    }

    @Test
    void testCreateContentThenUpdateContentAttributesWhenContentDoesNotExist() {
        var systemCode = UUID.randomUUID().toString();

        var groupType = UUID.randomUUID().toString();
        var groupCode = UUID.randomUUID().toString();

        var contentCode = UUID.randomUUID().toString();

        var newAttributes = Map.of(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        var rq = UpdateContentAttributesRq.builder()
                .systemCode(systemCode)
                .type(groupType)
                .groupCode(groupCode)
                .code(contentCode)
                .attributes(newAttributes)
                .build();

        var notFoundException = assertThrows(
                NotFoundException.class,
                () -> mutableContentService.updateContentAttributes(rq)
        );
        assertEquals("Content not found", notFoundException.getMessage());
    }

}
