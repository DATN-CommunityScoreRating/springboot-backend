package com.capstoneproject.server.payload.response;

import lombok.*;

import java.io.Serializable;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnlyIDDTO implements Serializable {
    private Long id;
}

