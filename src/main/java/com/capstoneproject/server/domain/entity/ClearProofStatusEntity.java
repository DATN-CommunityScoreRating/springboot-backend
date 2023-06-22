package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author dai.le-anh
 * @since 6/17/2023
 */

@Entity
@Table(name = "clear_proof_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClearProofStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long clearProofStatusId;

    private String name;

    private String code;

    private String description;
}
