package com.capstoneproject.server.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */
@Entity
@Getter
@Setter
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description")
    private String description;
}
