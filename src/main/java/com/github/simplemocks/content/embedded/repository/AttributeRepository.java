package com.github.simplemocks.content.embedded.repository;

import com.github.simplemocks.content.embedded.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author sibmaks
 * @since 0.0.1
 */
public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {

    /**
     * Get all attributes by content id
     *
     * @param contentId content identifier
     * @return list of content's attributes
     */
    List<AttributeEntity> findAllByContentId(long contentId);

    /**
     * Delete all attributes by content id
     *
     * @param contentId content identifier
     */
    void deleteAllByContentId(long contentId);
}
