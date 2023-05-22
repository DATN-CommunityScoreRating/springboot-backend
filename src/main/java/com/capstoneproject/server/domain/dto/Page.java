package com.capstoneproject.server.domain.dto;

import java.util.List;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

public class Page<T> {
    List<T> items;
    Long total;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Page(List<T> items, Long total) {
        this.items = items;
        this.total = total;
    }
}
