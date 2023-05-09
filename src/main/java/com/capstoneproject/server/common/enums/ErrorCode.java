package com.capstoneproject.server.common.enums;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */
public enum ErrorCode {
    REQUEST_METHOD_NOT_SUPPORT,
    INVALID_VALUE,
    REQUIRED_FIELD_MISSING,
    BAD_REQUEST,
    INTERNAL_ERROR,
    NOT_FOUND,

    NOT_NULL,


    // Internal

    RESOURCE_NOT_FOUND,
    NOT_EMPTY,
    DATE_FORMAT_INVALID,
    VALIDATE_FAILURE,
    DISCOUNT_EXPIRED,
    ALREADY_EXIST,
    EMAIL_INVALID,
    UNAUTHORIZED,
    AUTHENTICATION_FAILURE,
    ACCESS_DENIED,
    TOKEN_EXPIRED
}
