package com.capstoneproject.server.security;

import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.payload.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // handler exception
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        log.info("access denied custom");
        var responseObj = Response.newBuilder()
                .setSuccess(false)
                .setMessage("Bad request")
                .setErrorCode(ErrorCode.ACCESS_DENIED)
                .setException(accessDeniedException.getClass().getSimpleName())
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), responseObj);
    }

}