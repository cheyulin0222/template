package com.arplanet.template.log.enums;

public enum BasicActionType implements LoggingActionType {

    GET_REQUEST_DETAILS("request.get.details"),
    GET_RESPONSE_METADATA("response.get.metadata"),
    GET_RESPONSE_BODY("response.get.body"),
    VALIDATE_REQUEST("request.validate"),
    ACCESS_DATABASE("database.access"),
    AUTHENTICATE_USER("auth.authenticate"),
    REGISTER_USER("auth.register"),
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
