package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
