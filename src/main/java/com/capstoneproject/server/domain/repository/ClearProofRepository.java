package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ClearProofEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Repository
public interface ClearProofRepository extends JpaRepository<ClearProofEntity, Long> {
    @Query(value = "SELECT c FROM ClearProofEntity c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.clearProofId = :clearProofId " +
            "AND c.activityCategoryId IS NOT NULL")
    Optional<ClearProofEntity> findByIdAndFetchUser(@Param("clearProofId") Long clearProofId);

    @Query(value = "SELECT c FROM ClearProofEntity c " +
            "LEFT JOIN FETCH c.user " +
            "WHERE c.userActivityId = :userActivityId " +
            "AND c.activityCategoryId IS NULL")
    Optional<ClearProofEntity> findByUserActivityIdAndFetchUser(@Param("userActivityId") Long userActivityId);
}
