package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {
}
