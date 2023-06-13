package com.capstoneproject.server.domain.repository.dsl;

import com.capstoneproject.server.common.CommunityBKDNPrincipal;
import com.capstoneproject.server.common.enums.SortDirection;
import com.capstoneproject.server.common.enums.UserActivityStatus;
import com.capstoneproject.server.domain.dto.Page;
import com.capstoneproject.server.domain.entity.*;
import com.capstoneproject.server.domain.projection.ActivityProjection;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.payload.request.activity.MyActivityRequest;
import com.capstoneproject.server.payload.request.activity.UserActivityRequest;
import com.capstoneproject.server.util.RequestUtils;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Repository
@RequiredArgsConstructor
public class ActivityDslRepository {
    private final JPAQueryFactory queryBuilder;

    private final QUserEntity user = QUserEntity.userEntity;
    private final QActivityEntity activity = QActivityEntity.activityEntity;
    private final QUserActivityEntity userActivity = QUserActivityEntity.userActivityEntity;

    public Page<ActivityProjection> listActivity(ListActivitiesRequest request, CommunityBKDNPrincipal principal){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;
        JPAQuery<ActivityProjection> query = queryBuilder.select(Projections.constructor(ActivityProjection.class,
                        activity.activityId, activity.name, activity.startDate,
                        activity.endDate, activity.location, activity.maxQuantity,
                        queryBuilder.select(userActivity.count()).from(userActivity)
                                .where(userActivity.activity.activityId.eq(activity.activityId)),
                        activity.score, activity.createUserId, activity.startRegister, activity.endRegister,
                        queryBuilder.selectZero().from(userActivity).where(activity.activityId.eq(userActivity.activity.activityId))
                                .where(userActivity.user.userId.eq(principal.getUserId()))
                                .exists(),
                        queryBuilder.selectZero().from(userActivity).where(activity.activityId.eq(userActivity.activity.activityId))
                                .where(userActivity.status.status.eq(UserActivityStatus.SEND_PROOF.status))
                                .exists()))
                .from(activity);

        if (principal.isStudent()) {
            query.where(activity.startRegister.loe(DateTimeExpression.currentTimestamp(Timestamp.class)));
        }

        if (request.getIsRegistered() != null && request.getIsRegistered()){
            query.where(
                    queryBuilder.selectZero()
                            .from(userActivity)
                            .where(userActivity.activity.activityId.eq(activity.activityId))
                            .exists()
            );
        }

        JPAQuery<Long> countQuery = query.clone().select(activity.countDistinct());

        Order _order = StringUtils.isNotBlank(request.getDirection())
                && !SortDirection.isInvalid(request.getDirection())
                ? Order.valueOf(SortDirection.parse(request.getDirection()).shortName.toUpperCase())
                : Order.ASC;

        if (StringUtils.isNotBlank(request.getSort())){
            if (request.getSort().equals("score")){
                query.orderBy(new OrderSpecifier<>(_order, activity.score));
            } else if (request.getSort().equals("startDate")){
                query.orderBy(new OrderSpecifier<>(_order, activity.startDate));
            } else if (request.getSort().equals("quantity")) {
                query.orderBy(new OrderSpecifier<>(_order, activity.maxQuantity));
            } else {
                query.orderBy(new OrderSpecifier<>(_order, activity.startDate));
            }
        } else {
            query.orderBy(activity.modifyDate.desc());
        }

        query.limit(size).offset(offset);
        return new Page<>(query.fetch(), countQuery.fetchFirst());
    }

    public Page<Tuple> listUserActivity(Long activityId, UserActivityRequest request){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;

        JPAQuery<Tuple> query = queryBuilder.select(user, userActivity)
                .from(user, userActivity)
                .where(userActivity.user.userId.eq(user.userId))
                .where(userActivity.activity.activityId.eq(activityId));

        JPAQuery<Long> countQuery = query.clone().select(user.userId.countDistinct());

        Order _order = StringUtils.isNotBlank(request.getDirection())
                && !SortDirection.isInvalid(request.getDirection())
                ? Order.valueOf(SortDirection.parse(request.getDirection()).shortName.toUpperCase())
                : Order.ASC;

        query.leftJoin(user.clazz).fetchJoin();

        if (StringUtils.isNotBlank(request.getSort())){
            if (request.getSort().equals("score")){
                query.orderBy(new OrderSpecifier<>(_order, activity.score));
            } else if (request.getSort().equals("startDate")){
                query.orderBy(new OrderSpecifier<>(_order, activity.startDate));
            } else if (request.getSort().equals("quantity")) {
                query.orderBy(new OrderSpecifier<>(_order, activity.maxQuantity));
            } else {
                query.orderBy(new OrderSpecifier<>(_order, activity.startDate));
            }
        } else {
            query.orderBy(new OrderSpecifier<>(_order, user.clazz.faculty.facultyId),
                    new OrderSpecifier<>(_order, user.clazz.classId),
                    new OrderSpecifier<>(_order, user.username));
        }

        query.limit(size).offset(offset);

        return new Page<>(query.fetch(), countQuery.fetchFirst());
    }

    public Page<UserActivityEntity> findMyActivity(MyActivityRequest request, Long userId){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;

        JPAQuery<UserActivityEntity> query = queryBuilder.select(userActivity)
                .from(userActivity)
                .where(userActivity.user.userId.eq(userId)).orderBy(userActivity.activity.createDate.desc());

        JPAQuery<Long> countQuery = query.clone().select(userActivity.userActivityId.countDistinct());

        query.leftJoin(userActivity.activity);
        query.limit(size).offset(offset);

        return new Page<>(query.fetch(), countQuery.fetchFirst());

    }

}
