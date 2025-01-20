package com.arplanets.template.security.filter;

import com.arplanets.template.log.LogContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterFactory {

    @Autowired
    private LogContext logContext;
    @Autowired
    private ObjectMapper objectMapper;

    private LoggingFilter loggingFilter;
    private AuthLoggingFilter authLoggingFilter;

    public LoggingFilter getLoggingFilter() {
        if (loggingFilter == null) {
            loggingFilter = new LoggingFilter(logContext);
        }
        return loggingFilter;
    }

    public AuthLoggingFilter getAuthLoggingFilter() {
        if (authLoggingFilter == null) {
            authLoggingFilter = new AuthLoggingFilter(logContext, objectMapper);
        }
        return authLoggingFilter;
    }
}
