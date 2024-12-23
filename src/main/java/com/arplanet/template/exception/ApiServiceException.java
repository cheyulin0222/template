package com.arplanet.template.exception;

import com.arplanet.template.log.enums.LoggingActionType;
import lombok.Getter;

@Getter
public class ApiServiceException extends RuntimeException{

    private final ErrorType errorType;
    private final BusinessExceptionDisplay code;
    private final String errorMessage;
    private final LoggingActionType actionType;


    public ApiServiceException(BusinessExceptionDisplay code, LoggingActionType actionType) {
        super(code.description().concat("  ").concat(code.message()));
        this.code = code;
        this.errorMessage = "";
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }

    public ApiServiceException(BusinessExceptionDisplay code, LoggingActionType actionType, String errorMessage) {
        super(code.message().concat(" ").concat(errorMessage));
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }

    public ApiServiceException(BusinessExceptionDisplay code, LoggingActionType actionType, String errorMessage, Throwable cause) {
        super(code.message().concat(" ").concat(errorMessage),cause);
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
        this.actionType = actionType;
    }
}
