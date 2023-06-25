package com.capstoneproject.server.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author dai.le-anh
 * @since 6/23/2023
 */

@Embeddable
public class GradingKey implements Serializable {
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "subcategory_id")
    private Long subCategoryId;
}
