package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author dai.le-anh
 * @since 6/2/2023
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_activity_status")
public class UserActivityStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_activity_status_id")
    private Long userActivityStatusId;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;
}
