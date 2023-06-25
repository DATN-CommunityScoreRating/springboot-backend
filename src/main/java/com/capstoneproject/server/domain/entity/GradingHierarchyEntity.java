package com.capstoneproject.server.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author dai.le-anh
 * @since 6/23/2023
 */

@Table(name = "grading_hierarchy")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class GradingHierarchyEntity {

    @EmbeddedId
    private GradingKey key;

}
