package com.arplanets.template.security;

import com.arplanets.template.advice.ExceptionHandleAdvice;
import com.arplanets.template.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.arplanets.template.exception.ErrorType.AUTH;
import static com.arplanets.template.exception.ErrorType.SYSTEM;
import static com.arplanets.template.log.enums.BasicActionType.AUTHENTICATE_USER;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * 處理Spring Security 的 AuthenticationException
 */
@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)  {

        var errorResponse = ExceptionHandleAdvice.wrapperExceptionResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase());

        try {
            Logger.error("Authentication failed", AUTHENTICATE_USER, AUTH);
            response.setStatus(SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            var  out = response.getWriter();
            out.print(objectMapper.writeValueAsString(errorResponse.getBody()));
            out.flush();
        } catch (Exception e) {
            Logger.error("Failed to write error response", AUTHENTICATE_USER, SYSTEM, e);
        }
    }
}
