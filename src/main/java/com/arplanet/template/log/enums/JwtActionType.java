package com.arplanet.template.log.enums;

public enum JwtActionType implements LoggingActionType {

    INIT_PUBLIC_KEY("public.key.init"),
    VERIFY_JWT("jwt.verify");

    private final String action;

    JwtActionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
