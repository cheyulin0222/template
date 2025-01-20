package com.arplanets.template.log.enums;

public enum LoggingActionType implements ActionType {

    LOG_REQUEST_RESPONSE_INFO("request.response.log");


    private final String action;

    LoggingActionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
