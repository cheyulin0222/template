package com.arplanet.template.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    @Schema(description = "狀態")
    private Integer status;

    @Schema(description = "訊息")
    private String message;
}
