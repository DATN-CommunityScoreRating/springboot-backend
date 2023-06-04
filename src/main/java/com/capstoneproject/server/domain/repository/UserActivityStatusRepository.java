package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.UserActivityStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/4/2023
 */

@Repository
public interface UserActivityStatusRepository extends JpaRepository<UserActivityStatusEntity, Long> {
}
