package com.arplanets.template.security.filter;

import com.arplanets.template.log.Logger;
import com.arplanets.template.security.JwtVerifyService;
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

import static com.arplanets.template.exception.ErrorType.AUTH;
import static com.arplanets.template.log.enums.BasicActionType.AUTHENTICATE_USER;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtVerifyService jwtVerifyService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 取得 jwt
            String token = jwtVerifyService.extractToken(request);

            if (token != null) {
                // 驗證 token
                Claims claims = jwtVerifyService.verifyToken(token);

                // 若驗證成功，將資訊放入 Authentication 物件
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        token,
                        Collections.emptyList()
                );

                // 將 Authentication 物件放入 SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            Logger.error("Could not authenticate user", AUTHENTICATE_USER, AUTH);
        }

        filterChain.doFilter(request, response);
    }


}
