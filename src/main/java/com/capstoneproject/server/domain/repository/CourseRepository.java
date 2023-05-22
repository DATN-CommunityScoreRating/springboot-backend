package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
}
