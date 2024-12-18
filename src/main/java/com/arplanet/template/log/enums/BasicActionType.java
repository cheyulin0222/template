package com.arplanet.template.log.enums;

import com.arplanet.template.log.LoggingActionType;

public enum BasicActionType implements LoggingActionType {

    REQUEST_DETAILS("request.details"),
    RESPONSE_METADATA("response.metadata"),
    RESPONSE_BODY("response.body"),
    REQUEST_VALIDATION("request.validation"),
    DATABASE_ACCESS("database.access"),
    AUTHENTICATION("authentication"),
    UNKNOWN("unknown");

    private final String action;

    BasicActionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
