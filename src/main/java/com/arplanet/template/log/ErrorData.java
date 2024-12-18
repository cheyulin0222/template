package com.arplanet.template.log;

import com.arplanet.template.exception.ErrorType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorData {

    private String message;

    @JsonProperty("error_type")
    private ErrorType errorType;

    @JsonProperty("stack_trace")
    private String stackTrace;
}
