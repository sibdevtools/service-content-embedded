package com.github.simple_mocks.content.embedded.repository;

import com.github.simple_mocks.content.embedded.entity.ContentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface ContentGroupRepository extends JpaRepository<ContentGroupEntity, Long> {

    @Modifying
    @Query(
            value = "INSERT INTO content_service.content_group (code, type, system_id, created_at) " +
                    "SELECT :code, :type, :systemId, current_timestamp WHERE NOT EXISTS(" +
                    "SELECT 1 FROM content_service.content_group " +
                    "WHERE type = :type AND code = :code AND system_id = :systemId)",
            nativeQuery = true
    )
    void saveIfNotExists(
            @Param("systemId") long systemId,
            @Param("type") String type,
            @Param("code") String code
    );

    /**
     * Find a group if it exists by full identifier
     *
     * @param systemCode group's system code
     * @param type       group type
     * @param code       group code
     * @return optional with a found group or empty otherwise
     */
    Optional<ContentGroupEntity> findBySystem_CodeAndTypeAndCode(String systemCode, String type, String code);

    /**
     * Delete a group if it exists by full identifier
     *
     * @param systemCode group's system code
     * @param type       group type
     * @param code       group code
     */
    void deleteBySystem_CodeAndTypeAndCode(String systemCode, String type, String code);

}
