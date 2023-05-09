package com.capstoneproject.server.controller;

import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.exception.ValidateException;
import com.capstoneproject.server.payload.response.ErrorDTO;
import com.capstoneproject.server.payload.response.Response;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Log4j2
@RestControllerAdvice
@SuppressWarnings("unused")
public class CommonExceptionHandlingController extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("HttpRequestMethodNotSupportedException was rise and caught");
        log.warn("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

        Response<?> error = Response.newBuilder()
                .setSuccess(false)
                .setMessage("Request method not supported")
                .setErrorCode(ErrorCode.REQUEST_METHOD_NOT_SUPPORT)
                .setException(ex.getClass().getSimpleName())
                .setErrors(List.of(ErrorDTO.of(ex.getMethod(), ErrorCode.REQUEST_METHOD_NOT_SUPPORT)))
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("BindException was risen and caught.");
        log.warn("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        log.warn("Path: {}", request.getDescription(false).substring(4));

        final List<ErrorDTO> errors = new ArrayList<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            log.warn("{} - {}", error.getField(), error.getDefaultMessage());
            errors.add(ErrorDTO.of(error.getField(), error.getRejectedValue() == null || !StringUtils.hasText(error.getRejectedValue().toString()) ? ErrorCode.REQUIRED_FIELD_MISSING : ErrorCode.INVALID_VALUE));
        }

        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            log.warn("{} - {}", error.getObjectName(), error.getDefaultMessage());
            errors.add(ErrorDTO.of(error.getObjectName(), ErrorCode.INVALID_VALUE));
        }

        Response<?> error = Response.newBuilder()
                .setSuccess(false)
                .setMessage("Bad request")
                .setErrorCode(ErrorCode.BAD_REQUEST)
                .setException(ex.getClass().getSimpleName())
                .setErrors(errors)
                .build();

        return ResponseEntity.status(status).body(error);


    }

    @Override
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("NoHandlerFoundException was risen and caught.");
        log.warn("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        log.warn("Path: {}", request.getDescription(false).substring(4));
        log.warn(ex.getMessage());

        Response<?> error = Response.newBuilder()
                .setSuccess(false)
                .setErrorCode(ErrorCode.NOT_FOUND)
                .setMessage("No handler found for " + ex.getHttpMethod() + " - " + ex.getRequestURL())
                .setException(ex.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidateException.class)
    protected ResponseEntity<Object> handleValidateException(ValidateException exception){
        log.warn("MethodArgumentNotValidException was risen and caught");
        log.warn("Exception: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());

        var res = Response.newBuilder()
                .setSuccess(false)
                .setException(exception.getClass().getSimpleName())
                .setErrorCode(ErrorCode.VALIDATE_FAILURE)
                .setErrors(exception.getErrors())
                .setMessage(exception.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("MethodArgumentNotValidException was risen and caught");
        log.warn("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        log.warn("Path: {}", request.getDescription(false).substring(4));

        final List<ErrorDTO> errors = new ArrayList<>();

        Set<String> errorFields = new HashSet<>();

        for (final FieldError error : ex.getBindingResult().getFieldErrors()){
            if (!errorFields.contains(error.getField())){
                errorFields.add(error.getField());
                errors.add(ErrorDTO.of(error.getField(), error.getRejectedValue() == null || !StringUtils.hasText(error.getRejectedValue().toString()) ? ErrorCode.REQUIRED_FIELD_MISSING : ErrorCode.INVALID_VALUE));
            }
        }

        for (final ObjectError error : ex.getBindingResult().getFieldErrors()){
            log.warn("{} - {}" , error.getObjectName(), error.getDefaultMessage());
            errors.add(ErrorDTO.of(error.getObjectName(), ErrorCode.INVALID_VALUE));
        }

        Response<?> error = Response.newBuilder()
                .setSuccess(false)
                .setMessage("Bad request")
                .setErrorCode(ErrorCode.BAD_REQUEST)
                .setException(ex.getClass().getSimpleName())
                .setErrors(errors)
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e){
        log.warn("AccessDeniedException  was risen and caught");
        var responseObj = Response.newBuilder()
                .setSuccess(false)
                .setMessage("Bad request")
                .setErrorCode(ErrorCode.ACCESS_DENIED)
                .setException(e.getClass().getSimpleName())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseObj);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        log.warn("RuntimeException was risen and caught.");
        log.warn("Exception: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());
        log.warn(exception.getMessage());
        exception.printStackTrace();
        Response<?> error = Response.newBuilder()
                .setSuccess(false)
                .setMessage(exception.getMessage())
                .setErrorCode(ErrorCode.INTERNAL_ERROR)
                .setException(exception.getClass().getSimpleName())
                .build();

        return ResponseEntity.status(500).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("Uncaught exception falls back to default exception handler.");
        log.warn("Exception: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        log.warn("Path: {}", request.getDescription(false).substring(4));

        if (ex.getCause() != null) {
            log.warn("Cause: {} - {}", ex.getCause().getClass().getSimpleName(), ex.getCause().getMessage());
        }

        ex.printStackTrace();
        return ResponseEntity.status(status).body(Response.newBuilder()
                .setSuccess(false)
                .setErrorCode(ErrorCode.INTERNAL_ERROR)
                .setMessage(ex.getMessage())
                .setException(ex.getClass().getSimpleName())
                .build());
    }
}