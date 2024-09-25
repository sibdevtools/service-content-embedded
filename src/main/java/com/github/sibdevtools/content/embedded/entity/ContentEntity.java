package com.github.sibdevtools.content.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity(name = "content_attribute_content")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "content_service", name = "content")
public class ContentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "content", length = 1024 * 1024)
    private String content;
    @ManyToOne(optional = false)
    @JoinColumn(name = "content_group_id")
    private ContentGroupEntity group;
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    @Column(name = "modified_at", nullable = false)
    private ZonedDateTime modifiedAt;
}

