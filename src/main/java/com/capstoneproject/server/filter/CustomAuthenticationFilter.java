package com.capstoneproject.server.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.domain.entity.UserEntity;
import com.capstoneproject.server.domain.repository.UserRepository;
import com.capstoneproject.server.exception.ObjectNotFoundException;
import com.capstoneproject.server.payload.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final long EXPIRE_DURATION_ACCESS_TOKEN = 2 * 60 * 60 * 1000; // 1h
    private static final long EXPIRE_DURATION_REFRESH_TOKEN = 10 * 60 * 60 * 1000; // 10hrs

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    @Value("${app.security.secret.key}")
    private String secretKey;
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Response<Object> responseObj = Response.<Object>newBuilder()
                .setSuccess(false)
                .setMessage(failed.getMessage())
                .setErrorCode(ErrorCode.AUTHENTICATION_FAILURE)
                .setException(failed.getClass().getSimpleName())
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseObj);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            log.info("email is {}", email);
            log.info("Password is {}", password);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            return authenticationManager.authenticate(authToken);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();
        var account = userRepository.findByUsername(user.getUsername()).orElseThrow(() ->
                new ObjectNotFoundException("username", user.getUsername()));
        Algorithm algorithm = Algorithm.HMAC256(Constant.BYTE_CODE.getBytes());
        String access_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DURATION_ACCESS_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().findFirst().get().getAuthority())
                .withClaim("account_id", account.getUserId())
                .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DURATION_REFRESH_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
        Map<String, Object> tokens = new HashMap<>();
        tokens.put(Constant.ACCESS_TOKEN, access_token);
        tokens.put(Constant.REFRESH_TOKEN, refresh_token);


        response.setContentType(APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}