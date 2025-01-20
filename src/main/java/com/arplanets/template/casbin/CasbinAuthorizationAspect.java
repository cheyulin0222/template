package com.arplanets.template.casbin;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Aspect
@Component
@RequiredArgsConstructor
public class CasbinAuthorizationAspect {

    private final CasbinService casbinService;

    @Around("@annotation(casbinAuthorize)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint, CasbinAuthorize casbinAuthorize) throws Throwable {

        Class<?> targetClass = joinPoint.getTarget().getClass();

        String resource = casbinAuthorize.resource().isEmpty()
                ? targetClass.getAnnotation(CasbinResource.class).value()
                : casbinAuthorize.resource();

        String action = casbinAuthorize.action();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!casbinService.checkPermission(username, resource, action)) {
            throw new AccessDeniedException(
                    String.format("User does not have permission to %s %s",
                            action,
                            resource
                    )
            );
        }

        return joinPoint.proceed();
    }
}
