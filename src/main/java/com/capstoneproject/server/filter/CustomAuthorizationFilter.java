package com.capstoneproject.server.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.capstoneproject.server.common.constants.Constant;
import com.capstoneproject.server.common.enums.ErrorCode;
import com.capstoneproject.server.payload.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author lkadai0801
 * @since 09/05/2023
 */
@Slf4j
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private static final Pattern URI_ANONYMOUS_PATTERN = Pattern.compile("^/anonymous/.*");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (Constant.UN_AUTHENTICATION_PATH.contains(request.getServletPath()) ||
                URI_ANONYMOUS_PATTERN.matcher(request.getServletPath()).matches()){
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(Constant.PREFIX_TOKEN)) {
                try {
                    String token = authorizationHeader.substring(Constant.PREFIX_TOKEN.length());
                    Algorithm algorithm = Algorithm.HMAC256(Constant.BYTE_CODE.getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    //Neu token het han, ngoai le se xuan hien o day
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String role = decodedJWT.getClaim("roles").asString();
                    Collection<GrantedAuthority> authorities = new ArrayList<>();

                    authorities.add(new SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e){
                    // Handler expired token exception
                    log.warn("Token expired");
                    var responseObj = Response.newBuilder()
                            .setSuccess(false)
                            .setMessage("Token expired")
                            .setErrorCode(ErrorCode.TOKEN_EXPIRED)
                            .setException(e.getClass().getSimpleName())
                            .build();
                    response.setStatus(FORBIDDEN.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), responseObj);
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}