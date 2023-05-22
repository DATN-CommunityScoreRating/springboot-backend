package com.capstoneproject.server.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class CourseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "date_start")
    private Timestamp dateStart;
}
