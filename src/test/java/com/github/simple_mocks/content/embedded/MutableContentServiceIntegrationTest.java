package com.github.simple_mocks.content.embedded;

import com.github.simple_mocks.content.embedded.entity.AttributeEntity;
import com.github.simple_mocks.content.embedded.exception.NotFoundException;
import com.github.simple_mocks.content.embedded.repository.AttributeRepository;
import com.github.simple_mocks.content.embedded.repository.ContentGroupRepository;
import com.github.simple_mocks.content.embedded.repository.ContentRepository;
import com.github.simple_mocks.content.embedded.repository.SystemRepository;
import com.github.simple_mocks.content.mutable.api.rq.*;
import com.github.simple_mocks.content.mutable.api.service.MutableContentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@EnableContentServiceEmbedded
@SpringBootApplication
class MutableContentServiceIntegrationTest {
    @Test
    void testCreateSystemThenDelete() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

            var systemCode = UUID.randomUUID().toString();
            var createRq = CreateSystemRq.builder()
                    .systemCode(systemCode)
                    .build();
            mutableContentService.createSystem(createRq);
            mutableContentService.createSystem(createRq);

            var systemRepository = context.getBean(SystemRepository.class);
            var systemEntities = systemRepository.findAll();
            assertNotNull(systemEntities);
            assertEquals(1, systemEntities.size());

            var deleteRq = DeleteSystemRq.builder()
                    .systemCode(systemCode.toLowerCase())
                    .build();
            mutableContentService.deleteSystem(deleteRq);

            systemEntities = systemRepository.findAll();
            assertNotNull(systemEntities);
            assertEquals(0, systemEntities.size());
        }
    }

    @Test
    void testCreateContentGroupThenDelete() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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

            var contentGroupRepository = context.getBean(ContentGroupRepository.class);
            var groupEntities = contentGroupRepository.findAll();
            assertNotNull(groupEntities);
            assertEquals(1, groupEntities.size());

            mutableContentService.deleteContentGroup(
                    DeleteContentGroupRq.builder()
                            .systemCode(systemCode)
                            .type(groupType)
                            .groupCode(groupCode)
                            .build()
            );

            groupEntities = contentGroupRepository.findAll();
            assertNotNull(groupEntities);
            assertEquals(0, groupEntities.size());
        }
    }

    @Test
    void testCreateContentGroupWhenSystemDoesNotExist() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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
    }

    @Test
    void testCreateContentThenDelete() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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

            var contentRepository = context.getBean(ContentRepository.class);
            var contentEntities = contentRepository.findAll();
            assertNotNull(contentEntities);
            assertEquals(1, contentEntities.size());

            var deleteRq = DeleteContentRq.builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .code(contentCode)
                    .build();
            mutableContentService.deleteContent(deleteRq);

            contentEntities = contentRepository.findAll();
            assertNotNull(contentEntities);
            assertEquals(0, contentEntities.size());

            mutableContentService.deleteContent(deleteRq);
        }
    }

    @Test
    void testCreateContentThenUpdateContent() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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

            var contentRepository = context.getBean(ContentRepository.class);

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
    }

    @Test
    void testUpdateContentWhenContentDoesNotExist() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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
    }

    @Test
    void testCreateContentWhenContentGroupDoesNotExist() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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
    }

    @Test
    void testCreateContentThenUpdateContentAttributes() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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

            var contentRepository = context.getBean(ContentRepository.class);
            var contents = contentRepository.findAll();
            var contentId = contents.getFirst()
                    .getId();

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

            var attributeRepository = context.getBean(AttributeRepository.class);
            var attributeEntities = attributeRepository.findAllByContentId(contentId);
            assertEquals(3, attributeEntities.size());

            var attributesMap = attributeEntities.stream()
                    .collect(Collectors.toMap(AttributeEntity::getCode, AttributeEntity::getValue));

            assertEquals(toAddValue, attributesMap.get(toAddKey));
            assertEquals(toChangeNewValue, attributesMap.get(toChangeKey));
            assertEquals(notChangedValue, attributesMap.get(notChangedKey));
        }
    }

    @Test
    void testCreateContentThenUpdateContentAttributesWhenContentDoesNotExist() {
        try (var context = SpringApplication.run(MutableContentServiceIntegrationTest.class)) {
            assertNotNull(context);

            var mutableContentService = context.getBean(MutableContentService.class);

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

}
