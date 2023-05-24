package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.role " +
            "WHERE u.username = :username")
    Optional<UserEntity> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH u.clazz l " +
            "LEFT JOIN FETCH l.faculty " +
            "WHERE u.userId = :userId")

    Optional<UserEntity> findByIdAndFetchRoleFacultyAndClass(@Param("userId") Long userId);
}
