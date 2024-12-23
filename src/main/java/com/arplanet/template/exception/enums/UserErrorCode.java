package com.arplanet.template.exception.enums;

import com.arplanet.template.exception.BusinessExceptionDisplay;

public enum UserErrorCode implements BusinessExceptionDisplay {

    _001("帳號已存在");

    private final String message;

    UserErrorCode (String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String description() {
        return this.getClass().getSimpleName()+this.name();
    }
}
