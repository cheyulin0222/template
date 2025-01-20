package com.arplanets.template.log;

import com.arplanets.template.exception.ErrorType;
import com.arplanets.template.log.enums.ActionType;

import java.util.Map;

public class Logger {

    private static LoggingService loggingService;

    static void initializeLoggingService(LoggingService service) {
        loggingService = service;
    }

    public static void info(String message, ActionType actionType) {
        loggingService.info(message, actionType);
    }

    public static void info(String message, ActionType actionType, Map<String, Object> context) {
        loggingService.info(message, actionType, context);
    }

    public static void error(String message, ActionType actionType, ErrorType errorType) {
        loggingService.error(message, actionType, errorType);
    }

    public static void error(String message, ActionType actionType, ErrorType errorType, Map<String, Object> context) {
        loggingService.error(message, actionType, errorType, context);
    }

    public static void error(String message, ActionType actionType, ErrorType errorType, Throwable error) {
        loggingService.error(message, actionType, errorType, error);
    }

    public static void error(String message, ActionType actionType, ErrorType errorType, Throwable error, Map<String, Object> context) {
        loggingService.error(message, actionType, errorType, error, context);
    }
}
