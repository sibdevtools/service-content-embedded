package com.github.simplemocks.content.embedded;

import com.github.simplemocks.content.api.condition.EqualsCondition;
import com.github.simplemocks.content.api.condition.IsNullCondition;
import com.github.simplemocks.content.api.condition.NotEqualsCondition;
import com.github.simplemocks.content.api.condition.NotNullCondition;
import com.github.simplemocks.content.api.rq.GetContentRq;
import com.github.simplemocks.content.api.service.ContentService;
import com.github.simplemocks.content.mutable.api.rq.CreateContentGroupRq;
import com.github.simplemocks.content.mutable.api.rq.CreateContentRq;
import com.github.simplemocks.content.mutable.api.rq.CreateSystemRq;
import com.github.simplemocks.content.mutable.api.service.MutableContentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@ActiveProfiles("startup-test")
@EnableContentServiceEmbedded
@SpringBootApplication
class ContentServiceIntegrationTest {

    @Test
    void testCreateContentThenGetWithoutConditions() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            var contentHolder = foundContent.get(contentCode);
            assertNotNull(contentHolder);

            assertEquals(contentCode, contentHolder.getCode());
            assertEquals(content, contentHolder.getContent());
            assertEquals(attributes, contentHolder.getAttributes());
        }
    }

    @Test
    void testCreateContentThenGetWithEqCondition() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new EqualsCondition(attributeKey, attributeValue)
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            var contentHolder = foundContent.get(contentCode);

            assertEquals(contentCode, contentHolder.getCode());
            assertEquals(content, contentHolder.getContent());
            assertEquals(attributes, contentHolder.getAttributes());
        }
    }

    @Test
    void testCreateContentThenGetWithNotEqCondition() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new NotEqualsCondition(attributeKey, attributeValue + "-other")
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            var contentHolder = foundContent.get(contentCode);

            assertEquals(contentCode, contentHolder.getCode());
            assertEquals(content, contentHolder.getContent());
            assertEquals(attributes, contentHolder.getAttributes());
        }
    }

    @Test
    void testCreateContentThenGetWithIsNullCondition() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new IsNullCondition(attributeKey + "-other")
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            var contentHolder = foundContent.get(contentCode);

            assertEquals(contentCode, contentHolder.getCode());
            assertEquals(content, contentHolder.getContent());
            assertEquals(attributes, contentHolder.getAttributes());
        }
    }

    @Test
    void testCreateContentThenGetWithNotNullCondition() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new NotNullCondition(attributeKey)
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            var contentHolder = foundContent.get(contentCode);

            assertEquals(contentCode, contentHolder.getCode());
            assertEquals(content, contentHolder.getContent());
            assertEquals(attributes, contentHolder.getAttributes());
        }
    }

    @Test
    void testCreateContentThenGetWithEqConditionWhenNoContent() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new EqualsCondition(attributeKey, attributeValue + "-other")
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            assertTrue(foundContent.isEmpty());
        }
    }

    @Test
    void testCreateContentThenGetWithNotEqConditionWhenNoContent() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new NotEqualsCondition(attributeKey, attributeValue)
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            assertTrue(foundContent.isEmpty());
        }
    }

    @Test
    void testCreateContentThenGetWithIsNullConditionWhenNoContent() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new IsNullCondition(attributeKey)
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            assertTrue(foundContent.isEmpty());
        }
    }

    @Test
    void testCreateContentThenGetWithNotNullConditionWhenNoContent() {
        try (var context = SpringApplication.run(ContentServiceIntegrationTest.class)) {
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

            var attributeKey = UUID.randomUUID().toString();
            var attributeValue = UUID.randomUUID().toString();
            var attributes = Map.of(
                    attributeKey, attributeValue
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

            var contentService = context.getBean(ContentService.class);

            var rq = GetContentRq.<Map>builder()
                    .systemCode(systemCode)
                    .type(groupType)
                    .groupCode(groupCode)
                    .contentType(Map.class)
                    .conditions(List.of(
                            new NotNullCondition(attributeKey + "-other")
                    ))
                    .build();

            var foundContentRs = contentService.getContent(rq);
            var foundContent = foundContentRs.getBody();

            assertNotNull(foundContent);

            assertTrue(foundContent.isEmpty());
        }
    }
}
