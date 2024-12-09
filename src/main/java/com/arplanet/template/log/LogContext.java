package com.arplanet.template.log;

import com.arplanet.template.security.JwtVerifyService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class LogContext {
    
    private final HttpServletRequest request;
    private final JwtVerifyService jwtVerifyService;

    @Value("${cloud.aws.instance.id:unknown}")
    private String instanceId;

    @Value("${spring.profiles.active:unknown}")
    private String activeProfile;

    @Value("${git.commit.id.abbrev:UNKNOWN}")
    private String gitVersion;

    @Value("${spring.application.name}")
    private String project;

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

    public String getRequestId() {
        return (String) request.getAttribute("requestId");
    }

    public String getLogSn() {
        return generateId("log");
    }

    public String getMethod() {
        return request.getMethod();
    }

    public String getURI() {
        return request.getRequestURI();
    }

    public String getClassName() {
        return StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(2)
                        .findFirst()
                        .map(StackWalker.StackFrame::getClassName)
                        .orElse("Unknown"));
    }

    public String getMethodName() {
        return StackWalker.getInstance()
                .walk(frames -> frames
                        .skip(2)
                        .findFirst()
                        .map(StackWalker.StackFrame::getMethodName)
                        .orElse("Unknown"));
    }

    public String generateId(String prefix) {
        ZoneId taipeiZone = ZoneId.of("Asia/Taipei");
        String timestamp = LocalDateTime.now(taipeiZone)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return "%s-%s-%s-%s".formatted(prefix, getProject(), timestamp, UUID.randomUUID().toString());
    }
}
