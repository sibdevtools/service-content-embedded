package com.github.sibdevtools.content.embedded.repository;

import com.github.sibdevtools.content.embedded.entity.ContentEntity;
import com.github.sibdevtools.content.embedded.entity.ContentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

    /**
     * Get a content if it exists by full identifier
     *
     * @param systemCode group's system code
     * @param groupType  group type
     * @param groupCode  group code
     * @param code       content code
     * @return optional with a found content or empty otherwise
     */
    @Query(
            value = "SELECT content.* FROM content_service.content content " +
                    "LEFT JOIN content_service.content_group ON " +
                    "content_group.code = :groupCode AND content_group.type = :groupType " +
                    "LEFT JOIN content_service.system ON system.code = :systemCode " +
                    "WHERE content.code = :code",
            nativeQuery = true
    )
    Optional<ContentEntity> findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCodeAndLock(
            @Param("systemCode") String systemCode,
            @Param("groupType") String groupType,
            @Param("groupCode") String groupCode,
            @Param("code") String code
    );

    /**
     * Get a content if it exists by full identifier
     *
     * @param systemCode group's system code
     * @param groupType  group type
     * @param groupCode  group code
     * @param code       content code
     * @return optional with a found system or empty otherwise
     */
    Optional<ContentEntity> findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
            String systemCode,
            String groupType,
            String groupCode,
            String code
    );

    /**
     * Find all content in passed group
     *
     * @param contentGroup content group
     * @return found content
     */
    List<ContentEntity> findAllByGroup(ContentGroupEntity contentGroup);
}
