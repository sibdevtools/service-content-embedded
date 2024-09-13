package com.github.simplemocks.content.embedded.repository;

import com.github.simplemocks.content.embedded.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface SystemRepository extends JpaRepository<SystemEntity, Long> {

    /**
     * Find a system by code
     *
     * @param code system code
     * @return optional with a found system or empty otherwise
     */
    Optional<SystemEntity> findByCode(String code);

    /**
     * Save a new system by code if not exists.
     *
     * @param code system code
     */
    @Modifying
    @Query(
            value = "INSERT INTO content_service.system (code, created_at) " +
                    "SELECT :code, current_timestamp WHERE NOT EXISTS(" +
                    "SELECT 1 FROM content_service.system WHERE code = :code" +
                    ")",
            nativeQuery = true
    )
    void saveIfNotExists(@Param("code") String code);

    /**
     * Delete a system by code
     *
     * @param systemCode system code
     */
    void deleteByCode(String systemCode);
}
