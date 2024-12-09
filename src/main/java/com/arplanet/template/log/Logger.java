package com.arplanet.template.log;

import com.arplanet.template.exception.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Logger {

    private final LogContext logContext;
    private final ObjectMapper objectMapper;

    public void info(String message) {
        log(LogLevel.INFO, message, null, null);
    }

    public void info(String message, Map<String, Object> context) {
        log(LogLevel.INFO, message, context, null);
    }

    public void error(String message, ErrorType errorType) {
        ErrorData errorData = ErrorData.builder()
                .error(message)
                .errorType(errorType)
                .build();

        log(LogLevel.ERROR, message, null, errorData);
    }

    public void error(String message, Map<String, Object> payload, ErrorType errorType) {
        ErrorData errorData = ErrorData.builder()
                .error(message)
                .errorType(errorType)
                .build();

        log(LogLevel.ERROR, message, payload, errorData);
    }

    public void error(String message, Throwable error, ErrorType errorType) {
        ErrorData errorData = ErrorData.builder()
                .error(message + " | " + error.getMessage())
                .errorType(errorType)
                .stackTrace(Arrays.toString(error.getStackTrace()))
                .build();

        log(LogLevel.ERROR, message, null, errorData);
    }

    public void error(String message, Throwable error, Map<String, Object> context, ErrorType errorType) {
        ErrorData errorData = ErrorData.builder()
                .error(message + " | " + error.getMessage())
                .errorType(errorType)
                .stackTrace(Arrays.toString(error.getStackTrace()))
                .build();

        log(LogLevel.ERROR, message, context, errorData);
    }

    private void log(LogLevel level, String message, Map<String, Object> context, ErrorData errorData) {
        try {
            LogMessage logMessage = LogMessage.builder()
                    .project(logContext.getProject())
                    .stage(logContext.getActiveProfile())
                    .instanceId(logContext.getInstanceId())
                    .sessionId(logContext.getSessionId())
                    .requestId(logContext.getRequestId())
                    .logSn(logContext.getLogSn())
                    .method(logContext.getMethod())
                    .uri(logContext.getURI())
                    .logLevel(level.name().toLowerCase())
                    .className(logContext.getClassName())
                    .methodName(logContext.getMethodName())
                    .context(context)
                    .message(message)
                    .errorData(errorData)
                    .version(logContext.getGitVersion())
                    .timestamp(new Timestamp(
                            LocalDateTime.now(ZoneId.of("Asia/Taipei"))
                                    .toInstant(ZoneOffset.ofHours(8))
                                    .toEpochMilli()
                    ))
                    .build();

            String jsonLog = objectMapper.writeValueAsString(logMessage);

            switch (level) {
                case INFO -> log.info("{}", jsonLog);
                case ERROR -> log.error("{}", jsonLog);
                default -> log.debug("{}", jsonLog);
            }
        } catch (JsonProcessingException e) {
            log.error("Error creating JSON log", e);
        }
    }
}
