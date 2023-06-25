package com.capstoneproject.server.domain.repository.dsl;

import com.capstoneproject.server.common.CommunityBKDNPrincipal;
import com.capstoneproject.server.common.enums.SortDirection;
import com.capstoneproject.server.domain.dto.Page;
import com.capstoneproject.server.domain.entity.ClassEntity;
import com.capstoneproject.server.domain.entity.QClassEntity;
import com.capstoneproject.server.payload.request.GetAllClassRequest;
import com.capstoneproject.server.util.RequestUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */
@Repository
@RequiredArgsConstructor
public class ClassDslRepository {
    private final QClassEntity clazz = QClassEntity.classEntity;
    private final JPAQueryFactory queryFactory;

    public Page<ClassEntity> getListClass(GetAllClassRequest request, CommunityBKDNPrincipal principal, Long facultyId){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;

        JPAQuery<ClassEntity> query = queryFactory.select(clazz)
                .from(clazz);

        if (principal.isFaculty() || principal.isUnion()){
            query.where(clazz.faculty.facultyId.eq(facultyId));
        }

        if (request.getCourseId() != null && request.getCourseId() > 0){
            query.where(clazz.courseEntity.courseId.eq(request.getCourseId()));
        }

        if (request.getFacultyId() != null && request.getFacultyId() > 0){
            query.where(clazz.faculty.facultyId.eq(request.getFacultyId()));
        }

        if (StringUtils.isNotBlank(request.getSearchTerm())){
            query.where(clazz.className.contains(request.getSearchTerm()));
        }

        JPAQuery<Long> countQuery = query.clone().select(clazz.classId.countDistinct());

        Order _order = StringUtils.isNotBlank(request.getDirection())
                && !SortDirection.isInvalid(request.getDirection())
                ? Order.valueOf(SortDirection.parse(request.getDirection()).shortName.toUpperCase())
                : Order.ASC;

        if (StringUtils.isNotBlank(request.getSort())){
            if (request.getSort().equals("course")){
                query.orderBy(new OrderSpecifier<>(_order, clazz.courseEntity.courseId));
            } else if (request.getSort().equals("faculty")){
                query.orderBy(new OrderSpecifier<>(_order, clazz.faculty.facultyId));
            } else if (request.getSort().equals("className")) {
                query.orderBy(new OrderSpecifier<>(_order, clazz.className));
            } else {
                query.orderBy(new OrderSpecifier<>(_order, clazz.faculty.facultyId));
            }
        } else {
            query.orderBy(new OrderSpecifier<>(_order, clazz.faculty.facultyId));
        }

        query.leftJoin(clazz.faculty).fetchJoin();
        query.leftJoin(clazz.courseEntity).fetchJoin();

        query.offset(offset).limit(size);

        return new Page<>(query.fetch(), countQuery.fetchFirst());

    }
}
