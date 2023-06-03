package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "activities")
public class ActivityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name = "name")
    private String name;

    @Column(name = "score")
    private Integer score;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Column(name = "start_register")
    private Timestamp startRegister;

    @Column(name = "end_register")
    private Timestamp endRegister;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "create_user_id")
    private Long createUserId;
}
