package com.capstoneproject.server.domain.repository;

import com.capstoneproject.server.domain.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    @Query("SELECT c FROM ClassEntity c " +
            "LEFT JOIN FETCH c.faculty " +
            "LEFT JOIN FETCH c.courseEntity " +
            "WHERE c.classId = :classId")
    Optional<ClassEntity> findByIdAndFetchFacultyAndCourse(@Param("classId") Long classId);

    @Query("SELECT c FROM ClassEntity c " +
            "WHERE c.className in (:classNames)")
    List<ClassEntity> findAllByClassNames(@Param("classNames") Collection<String> classNames);
}
