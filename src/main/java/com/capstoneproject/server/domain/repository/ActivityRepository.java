package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, Long> {

    @Query("SELECT a, (SELECT COUNT(*) FROM UserActivityEntity u WHERE u.activity.activityId = a.activityId) FROM ActivityEntity a " +
            "WHERE a.activityId = :activityId")
    Optional<List<Tuple>> findByIdAndFetchUser(@Param("activityId") Long activityId);
}
