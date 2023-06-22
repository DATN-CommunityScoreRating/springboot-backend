package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Table(name = "clear_proof")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClearProofEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clear_proof_id")
    private Long clearProofId;

    @Column(name = "activity_category_id")
    private Long activityCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_activity_id")
    private Long userActivityId;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "score")
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    private ClearProofStatusEntity status;

}
