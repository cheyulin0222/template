package com.arplanet.template.security.filter;

import com.arplanet.template.log.LogContext;
import com.arplanet.template.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterFactory {

    @Autowired
    private Logger logger;
    @Autowired
    private LogContext logContext;
    @Autowired
    private ObjectMapper objectMapper;

    private LoggingFilter loggingFilter;
    private AuthLoggingFilter authLoggingFilter;

    public LoggingFilter getLoggingFilter() {
        if (loggingFilter == null) {
            loggingFilter = new LoggingFilter(logger, logContext);
        }
        return loggingFilter;
    }

    public AuthLoggingFilter getAuthLoggingFilter() {
        if (authLoggingFilter == null) {
            authLoggingFilter = new AuthLoggingFilter(logger, logContext, objectMapper);
        }
        return authLoggingFilter;
    }
}
