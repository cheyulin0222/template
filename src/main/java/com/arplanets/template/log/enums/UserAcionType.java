package com.arplanets.template.log.enums;

public enum UserAcionType implements ActionType {

    CREATE_USER("user.create"),
    GET_USER("user.get"),
    UPDATE_USER("user.update"),
    DELETE_USER("user.delete");

    private final String action;

    UserAcionType(String action) {
        this.action = action;
    }

    @Override
    public String getAction() {
        return action;
    }
}
