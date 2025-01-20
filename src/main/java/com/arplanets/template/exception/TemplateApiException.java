package com.arplanets.template.exception;

import com.arplanets.template.log.enums.ActionType;
import lombok.Getter;

@Getter
public class TemplateApiException extends RuntimeException{

    private final ErrorType errorType;
    private final BusinessExceptionDisplay code;
    private final String errorMessage;
    private final ActionType actionType;


    public TemplateApiException(BusinessExceptionDisplay code, ActionType actionType) {
        super(code.description().concat("  ").concat(code.message()));
        this.code = code;
        this.errorMessage = "";
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }

    public TemplateApiException(BusinessExceptionDisplay code, ActionType actionType, String errorMessage) {
        super(code.message().concat(" ").concat(errorMessage));
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }

    public TemplateApiException(BusinessExceptionDisplay code, ActionType actionType, String errorMessage, Throwable cause) {
        super(code.message().concat(" ").concat(errorMessage),cause);
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }
}
