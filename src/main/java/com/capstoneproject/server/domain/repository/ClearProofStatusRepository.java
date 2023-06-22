package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ClearProofStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Repository
public interface ClearProofStatusRepository extends JpaRepository<ClearProofStatusEntity, Long> {
}
