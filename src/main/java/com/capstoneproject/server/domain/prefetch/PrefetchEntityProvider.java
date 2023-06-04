package com.capstoneproject.server.domain.prefetch;

import com.capstoneproject.server.domain.entity.CourseEntity;
import com.capstoneproject.server.domain.entity.FacultyEntity;
import com.capstoneproject.server.domain.entity.RoleEntity;
import com.capstoneproject.server.domain.entity.UserActivityStatusEntity;
import com.capstoneproject.server.domain.repository.CourseRepository;
import com.capstoneproject.server.domain.repository.FacultyRepository;
import com.capstoneproject.server.domain.repository.RoleRepository;
import com.capstoneproject.server.domain.repository.UserActivityStatusRepository;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dai.le-anh
 * @since 5/28/2023
 */


@Component
@Getter
public class PrefetchEntityProvider {
    private Map<Long, RoleEntity> roleEntityMap;
    private Map<String, RoleEntity> roleEntityNameMap;

    private Map<Long, FacultyEntity> facultyEntityMap;
    private Map<String, FacultyEntity> facultyEntityCodeMap;

    private Map<Long, CourseEntity> courseEntityMap;
    private Map<String, CourseEntity> courseEntityCodeMap;
    private Map<Long, UserActivityStatusEntity> userActivityStatusEntityMap;
    private Map<String, UserActivityStatusEntity> userActivityStatusEntityCodeMap;

    public PrefetchEntityProvider(RoleRepository roleRepository,
                                  FacultyRepository facultyRepository,
                                  CourseRepository courseRepository,
                                  UserActivityStatusRepository userActivityStatusRepository) {
        var roles = roleRepository.findAll();

        this.roleEntityMap = roles.stream().collect(Collectors.toMap(RoleEntity::getRoleId, r -> r));
        this.roleEntityNameMap = roles.stream().collect(Collectors.toMap(RoleEntity::getRoleName, r -> r));

        var faculties = facultyRepository.findAll();

        this.facultyEntityMap = faculties.stream().collect(Collectors.toMap(FacultyEntity::getFacultyId, f -> f));
        this.facultyEntityCodeMap = faculties.stream().collect(Collectors.toMap(FacultyEntity::getCode, f -> f));

        var courses = courseRepository.findAll();

        this.courseEntityMap = courses.stream().collect(Collectors.toMap(CourseEntity::getCourseId, c -> c));
        this.courseEntityCodeMap = courses.stream().collect(Collectors.toMap(CourseEntity::getCode, c -> c));

        var userActivityStatus = userActivityStatusRepository.findAll();

        this.userActivityStatusEntityMap = userActivityStatus.stream().collect(Collectors.toMap(UserActivityStatusEntity::getUserActivityStatusId, c -> c));
        this.userActivityStatusEntityCodeMap = userActivityStatus.stream().collect(Collectors.toMap(UserActivityStatusEntity::getStatus, c -> c));
    }
}
