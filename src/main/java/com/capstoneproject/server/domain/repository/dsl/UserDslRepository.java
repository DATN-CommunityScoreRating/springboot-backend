package com.capstoneproject.server.domain.repository.dsl;

import com.capstoneproject.server.common.enums.SortDirection;
import com.capstoneproject.server.domain.dto.Page;
import com.capstoneproject.server.domain.entity.QClassEntity;
import com.capstoneproject.server.domain.entity.QUserEntity;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 5/23/2023
 */

@Repository
@RequiredArgsConstructor
public class UserDslRepository {
    private final QUserEntity user = QUserEntity.userEntity;
    private final QClassEntity clazz = QClassEntity.classEntity;

    private final JPAQueryFactory queryBuilder;

    public Page<UserEntity> getAllUser(int page, int size, String sort, String direction,
                                        Long classId, Long roleId, String searchTerm, Long facultyId){
        int offset = page * size;

        JPAQuery<UserEntity> query = queryBuilder.select(user)
                .from(user);

        if (classId != null && classId > 0){
            query.where(user.clazz.classId.eq(classId));
        }

        if (facultyId != null && facultyId > 0){
            query.where(user.clazz.faculty.facultyId.eq(facultyId));
        }

        if (roleId != null && roleId > 0){
            query.where(user.role.roleId.eq(roleId));
        }

        if (StringUtils.isNotBlank(searchTerm)){
            query.where(user.fullName.containsIgnoreCase(searchTerm).or(user.studentId.containsIgnoreCase(searchTerm)));
        }

        JPAQuery<Long> countQuery = query.clone().select(user.userId.countDistinct());

        Order _order = StringUtils.isNotBlank(direction)
                && !SortDirection.isInvalid(direction)
                ? Order.valueOf(SortDirection.parse(direction).shortName.toUpperCase())
                : Order.ASC;

        if (StringUtils.isNotBlank(sort)){
            if (sort.equalsIgnoreCase("fullName")){
                query.orderBy(new OrderSpecifier<>(_order, user.fullName));
            } else if ((sort.equalsIgnoreCase("class"))){
                query.orderBy(new OrderSpecifier<>(_order, user.clazz.classId));
            } else if (sort.equalsIgnoreCase("studentId")){
                query.orderBy(new OrderSpecifier<>(_order, user.studentId));
            } else if (sort.equalsIgnoreCase("role")){
                query.orderBy(new OrderSpecifier<>(_order, user.role.roleId));
            } else {
                query.orderBy(new OrderSpecifier<>(_order, user.fullName));
            }
        } else {
            query.orderBy(new OrderSpecifier<>(_order, user.role.roleId));
        }
        query.leftJoin(user.role).fetchJoin();
        query.leftJoin(user.clazz).fetchJoin();

        query.limit(size).offset(offset);

        return new Page<>(query.fetch(), countQuery.fetchFirst());
    }
}
