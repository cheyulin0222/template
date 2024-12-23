package com.arplanet.template.security.filter;

import com.arplanet.template.log.Logger;
import com.arplanet.template.security.JwtVerifyService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.arplanet.template.exception.ErrorType.AUTH;
import static com.arplanet.template.log.enums.BasicActionType.AUTHENTICATE_USER;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtVerifyService jwtVerifyService;
    private final Logger logger;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtVerifyService.extractToken(request);

            if (token != null) {
                Claims claims = jwtVerifyService.verifyToken(token);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        token,
                        Collections.emptyList()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Could not authenticate user", AUTHENTICATE_USER, e, AUTH);
        }

        filterChain.doFilter(request, response);
    }


}
