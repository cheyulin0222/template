package com.arplanets.template.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserUpdateResponse {

    @Schema(description = "帳號")
    private String email;

    @Schema(description = "修改結果")
    private String message;
}
