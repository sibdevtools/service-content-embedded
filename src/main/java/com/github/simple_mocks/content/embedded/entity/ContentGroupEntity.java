package com.github.simple_mocks.content.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "content_service", name = "content_group")
public class ContentGroupEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "system_id")
    private SystemEntity system;
    @Column(name = "code", nullable = false)
    private String code;
    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}

