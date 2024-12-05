package com.arplanet.template.log;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
public class LogMessage {

    private String project;

    private String instanceId;

    private String sessionId;

    private String requestId;

    private String logSn;

    private Map<String, Object> payload;

    private String version;

    @JsonSerialize(using = CustomTimestampSerializer.class)
    private Timestamp timestamp;
}
