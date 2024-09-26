package com.github.sibdevtools.content.embedded.repository;

import com.github.sibdevtools.content.embedded.entity.ContentEntity;
import com.github.sibdevtools.content.embedded.entity.ContentGroupEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

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
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default Optional<ContentEntity> findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCodeAndLock(
            String systemCode,
            String groupType,
            String groupCode,
            String code
    ) {
        return findByGroup_System_CodeAndGroup_TypeAndGroup_CodeAndCode(
                systemCode,
                groupType,
                groupCode,
                code
        );
    }

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
