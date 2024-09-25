CREATE SCHEMA IF NOT EXISTS content_service;

SET SCHEMA content_service;

CREATE TABLE IF NOT EXISTS system
(
    id         bigint                  NOT NULL AUTO_INCREMENT,
    code       VARCHAR_IGNORECASE(512) NOT NULL UNIQUE,
    created_at timestamp               NOT NULL,
    CONSTRAINT system_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS content_group
(
    id         bigint                  NOT NULL AUTO_INCREMENT,
    system_id  bigint                  NOT NULL,
    code       VARCHAR_IGNORECASE(512) NOT NULL,
    type       VARCHAR_IGNORECASE(255) NOT NULL,
    created_at timestamp               NOT NULL,
    CONSTRAINT content_group_pk PRIMARY KEY (id),
    FOREIGN KEY (system_id) REFERENCES system (id)
);

CREATE UNIQUE INDEX content_group_idx ON content_group (system_id, code, type);

CREATE TABLE IF NOT EXISTS content
(
    id               bigint                  NOT NULL AUTO_INCREMENT,
    code             VARCHAR_IGNORECASE(512) NOT NULL,
    content          text                    NOT NULL,
    content_group_id bigint                  NOT NULL,
    created_at       timestamp               NOT NULL,
    modified_at      timestamp               NOT NULL,
    CONSTRAINT content_pk PRIMARY KEY (id),
    FOREIGN KEY (content_group_id) REFERENCES content_group (id)
);

CREATE UNIQUE INDEX content_idx ON content (content_group_id, code);

CREATE TABLE IF NOT EXISTS content_attribute
(
    id          bigint                 NOT NULL AUTO_INCREMENT,
    code        character varying(512) NOT NULL,
    attr_value  character varying(512) NOT NULL,
    content_id  bigint                 NOT NULL,
    created_at  timestamp              NOT NULL,
    modified_at timestamp              NOT NULL,
    CONSTRAINT content_attribute_pk PRIMARY KEY (id),
    FOREIGN KEY (content_id) REFERENCES content (id)
);
