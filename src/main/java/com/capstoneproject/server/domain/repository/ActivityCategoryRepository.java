package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ActivityCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@Repository
public interface ActivityCategoryRepository extends JpaRepository<ActivityCategoryEntity, Long> {
}
