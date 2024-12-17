package com.arplanet.template.exception;

import lombok.Getter;

@Getter
public class TemplateException extends RuntimeException{

    private final ErrorType errorType;
    private final BusinessExceptionDisplay code;
    private final String errorMessage;


    public TemplateException(BusinessExceptionDisplay code) {
        super(code.description().concat("  ").concat(code.message()));
        this.code = code;
        this.errorMessage = "";
        this.errorType = ErrorType.BUSINESS;
    }

    public TemplateException(BusinessExceptionDisplay code, String errorMessage) {
        super(code.message().concat(" ").concat(errorMessage));
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
    }

    public TemplateException(BusinessExceptionDisplay code, String errorMessage,Throwable cause) {
        super(code.message().concat(" ").concat(errorMessage),cause);
        this.code = code;
        this.errorMessage = errorMessage;
        this.errorType = ErrorType.BUSINESS;
    }
}
