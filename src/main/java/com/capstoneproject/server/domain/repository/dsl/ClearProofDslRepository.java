package com.capstoneproject.server.domain.repository.dsl;

import com.capstoneproject.server.common.CommunityBKDNPrincipal;
import com.capstoneproject.server.domain.dto.Page;
import com.capstoneproject.server.domain.entity.*;
import com.capstoneproject.server.payload.request.clearProof.ListClearProofRequest;
import com.capstoneproject.server.util.RequestUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/20/2023
 */

@Repository
@RequiredArgsConstructor
public class ClearProofDslRepository {
    private final JPAQueryFactory queryBuilder;
    private final QClearProofEntity clearProof = QClearProofEntity.clearProofEntity;
    private final QUserEntity user = QUserEntity.userEntity;
    private final QActivityCategoryEntity activityCategory = QActivityCategoryEntity.activityCategoryEntity;
    private final QActivitySubCategoryEntity activitySubCategory = QActivitySubCategoryEntity.activitySubCategoryEntity;


    public Page<ClearProofEntity> listClearProof(ListClearProofRequest request, CommunityBKDNPrincipal principal){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;

        JPAQuery<ClearProofEntity> query = queryBuilder.select(clearProof).from(clearProof)
                .where(clearProof.activityCategoryId.isNotNull())
                .orderBy(clearProof.status.clearProofStatusId.asc(), clearProof.clearProofId.desc());

        if (request.getCategoryId() != null && request.getCategoryId() > 0){
            query.where(clearProof.activityCategoryId.eq(request.getCategoryId()));
        }

        if (request.getFacultyId() != null && request.getFacultyId() > 0){
            query.where(clearProof.user.clazz.faculty.facultyId.eq(request.getFacultyId()));
        }

        if (principal.isStudent()){
            query.where(clearProof.user.userId.eq(principal.getUserId()));
        }

        JPAQuery<Long> countQuery = query.clone().select(clearProof.clearProofId.countDistinct());
        query.leftJoin(clearProof.user, user).fetchJoin();
        query.leftJoin(user.clazz).fetchJoin();
        query.limit(size).offset(offset);
        return new Page<>(query.fetch(), countQuery.fetchFirst());
    }

}
