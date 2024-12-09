package com.arplanet.template.log;

import com.arplanet.template.serializer.TimestampToTaipeiISOSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
public class LogMessage {

    private String project;

    private String stage;

    private String instanceId;

    private String sessionId;

    private String requestId;

    private String logSn;

    private String method;

    private String uri;

    private String logLevel;

    private String className;

    private String methodName;

    private String message;

    private Map<String, Object> context;

    private ErrorData errorData;

    private String version;

    @JsonSerialize(using = TimestampToTaipeiISOSerializer.class)
    private Timestamp timestamp;
}
