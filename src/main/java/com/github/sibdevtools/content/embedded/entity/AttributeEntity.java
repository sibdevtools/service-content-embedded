package com.github.sibdevtools.content.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity(name = "content_attribute_content_attribute")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "content_service", name = "content_attribute")
public class AttributeEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "attr_value", length = 512)
    private String value;
    @Column(name = "content_id", nullable = false)
    private long contentId;
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    @Column(name = "modified_at", nullable = false)
    private ZonedDateTime modifiedAt;
}

