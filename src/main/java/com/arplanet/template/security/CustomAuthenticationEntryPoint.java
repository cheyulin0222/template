package com.arplanet.template.security;

import com.arplanet.template.advice.ExceptionHandleAdvice;
import com.arplanet.template.exception.ErrorType;
import com.arplanet.template.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import static com.arplanet.template.log.enums.BasicActionType.AUTHENTICATION;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger logger;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)  {

        var errorResponse = ExceptionHandleAdvice.wrapperExceptionResponse(HttpStatus.UNAUTHORIZED, "Unauthorized");

        try {
            logger.error("Authentication error", AUTHENTICATION, authException, ErrorType.AUTH);
            response.setStatus(SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            var  out = response.getWriter();
            out.print(objectMapper.writeValueAsString(errorResponse.getBody()));
            out.flush();
        } catch (Exception e) {
            logger.error("Authentication failed", AUTHENTICATION, e, ErrorType.AUTH);
        }
    }
}
