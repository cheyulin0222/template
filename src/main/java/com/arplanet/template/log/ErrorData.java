package com.arplanet.template.log;

import com.arplanet.template.exception.ErrorType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorData {

    private String error;

    private ErrorType errorType;

    private String stackTrace;
}
