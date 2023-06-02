package com.capstoneproject.server.domain.repository.dsl;

import com.capstoneproject.server.common.enums.SortDirection;
import com.capstoneproject.server.domain.dto.Page;
import com.capstoneproject.server.domain.entity.QActivityEntity;
import com.capstoneproject.server.domain.entity.QUserActivityEntity;
import com.capstoneproject.server.domain.entity.QUserActivityStatusEntity;
import com.capstoneproject.server.domain.projection.ActivityProjection;
import com.capstoneproject.server.payload.request.activity.ListActivitiesRequest;
import com.capstoneproject.server.util.RequestUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Repository
@RequiredArgsConstructor
public class ActivityDslRepository {
    private final JPAQueryFactory queryBuilder;
    private final QActivityEntity activity = QActivityEntity.activityEntity;
    private final QUserActivityStatusEntity userActivityStatus = QUserActivityStatusEntity.userActivityStatusEntity;
    private final QUserActivityEntity userActivity = QUserActivityEntity.userActivityEntity;

    public Page<ActivityProjection> listActivity(ListActivitiesRequest request){
        var page = RequestUtils.getPage(request.getPage());
        var size = RequestUtils.getSize(request.getSize());
        var offset = page * size;
        JPAQuery<ActivityProjection> query = queryBuilder.select(Projections.constructor(ActivityProjection.class,
                        activity.activityId, activity.name, activity.startDate,
                        activity.endDate, activity.location, activity.maxQuantity,
                        queryBuilder.select(userActivity.count()).from(userActivity)
                                .where(userActivity.activity.activityId.eq(activity.activityId)),
                        activity.score))
                .from(activity);
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
            query.orderBy(new OrderSpecifier<>(_order, activity.score));
        }

        query.limit(size).offset(offset);
        return new Page<>(query.fetch(), countQuery.fetchFirst());
    }

}
