package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ActivitySubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@Repository
public interface ActivitySubCategoryRepository extends JpaRepository<ActivitySubCategoryEntity, Long> {
    List<ActivitySubCategoryEntity> findAllByActivityCategoryId(Long activityCategoryId);
}
