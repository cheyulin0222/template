package com.arplanet.template.log;

import com.arplanet.template.security.JwtVerifyService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogContext {
    
    private final HttpServletRequest request;
    private final JwtVerifyService jwtVerifyService;

    public String getSessionId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() instanceof String token) {
                Claims claims = jwtVerifyService.extractPayloadWithoutVerification(token);

                String loginSessionId = claims.get("login_session_id", String.class);
                if (loginSessionId != null) {
                    return loginSessionId;
                }

                String username = claims.getSubject();
                Long loginTime = claims.get("initial_login_time", Long.class);
                if (username != null && loginTime != null) {
                    return username + "-" + loginTime;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to extract session id from token", e);
        }

        return null;
    }
}
