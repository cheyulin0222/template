package com.arplanet.template.log.enums;

public enum UserAcionType implements LoggingActionType {

    CREATE_USER("user.create");

    private final String action;

    UserAcionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
