package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author dai.le-anh
 * @since 6/19/2023
 */

@Entity
@Table(name = "activity_subcategories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ActivitySubCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_subcategory_id")
    private Long activitySubCategoryId;

    private String name;

    @Column(name = "activity_category_id")
    private Long activityCategoryId;

    @Column(name = "range_score")
    private String rangeScore;
}
