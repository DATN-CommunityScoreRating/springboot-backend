package com.capstoneproject.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@AllArgsConstructor
@Getter
public class CommunityException extends RuntimeException{
    protected final boolean isClientError;
}
