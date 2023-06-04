package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.UserActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dai.le-anh
 * @since 6/4/2023
 */

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivityEntity, Long> {
    @Query(value = "SELECT u.user.userId FROM UserActivityEntity u " +
            "WHERE u.activity.activityId = :activityId")
    List<Long> getAllUserIdRegistedActivity(@Param("activityId") Long activityId);
}
