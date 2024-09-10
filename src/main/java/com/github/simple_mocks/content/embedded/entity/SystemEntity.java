package com.github.simple_mocks.content.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity(name = "content_attribute_system")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "content_service", name = "system")
public class SystemEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "code", nullable = false, unique = true, length = 512)
    private String code;
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}

