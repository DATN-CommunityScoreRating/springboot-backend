package com.capstoneproject.server.exception;

import com.capstoneproject.server.payload.response.ErrorDTO;
import lombok.Getter;

import java.util.List;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Getter
public class ValidateException extends RuntimeException{
    private final String message = "Validate failure";
    private final List<ErrorDTO> errors;

    public ValidateException(List<ErrorDTO> errors){
        super();
        this.errors = errors;
    }
}

