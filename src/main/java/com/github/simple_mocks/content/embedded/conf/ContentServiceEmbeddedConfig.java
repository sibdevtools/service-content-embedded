package com.github.simple_mocks.content.embedded.conf;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.simple_mocks.content.api.service.ContentService;
import com.github.simple_mocks.content.embedded.codec.JsonContentCodec;
import com.github.simple_mocks.content.embedded.repository.AttributeRepository;
import com.github.simple_mocks.content.embedded.repository.ContentGroupRepository;
import com.github.simple_mocks.content.embedded.repository.ContentRepository;
import com.github.simple_mocks.content.embedded.repository.SystemRepository;
import com.github.simple_mocks.content.embedded.service.ContentServiceEmbedded;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@PropertySource("classpath:embedded-content-application.properties")
public class ContentServiceEmbeddedConfig {

    @Bean
    @ConfigurationProperties("spring.flyway.embedded-content")
    public ClassicConfiguration contentEmbeddedFlywayConfiguration(DataSource dataSource) {
        var classicConfiguration = new ClassicConfiguration();
        classicConfiguration.setDataSource(dataSource);
        return classicConfiguration;
    }

    @Bean
    public Flyway contentEmbeddedFlyway(
            @Qualifier("contentEmbeddedFlywayConfiguration") ClassicConfiguration configuration
    ) {
        var flyway = new Flyway(configuration);
        flyway.migrate();
        return flyway;
    }

    @Bean("contentServiceObjectMapper")
    public ObjectMapper contentServiceObjectMapper() {
        return JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
    }

    @Bean
    public JsonContentCodec jsonContentCodec(
            @Qualifier("contentServiceObjectMapper")
            ObjectMapper objectMapper
    ) {
        return new JsonContentCodec(objectMapper);
    }


    @Bean
    public ContentService contentServiceEmbedded(
            AttributeRepository attributeRepository,
            ContentRepository contentRepository,
            ContentGroupRepository contentGroupRepository,
            SystemRepository systemRepository,
            JsonContentCodec jsonContentCodec
    ) {
        return new ContentServiceEmbedded(
                attributeRepository,
                contentRepository,
                contentGroupRepository,
                systemRepository,
                jsonContentCodec
        );
    }

}
