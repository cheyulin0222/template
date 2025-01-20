package com.arplanets.template.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public class UserDeleteResponse {

    @Schema(description = "刪除結果")
    private String message;
}
