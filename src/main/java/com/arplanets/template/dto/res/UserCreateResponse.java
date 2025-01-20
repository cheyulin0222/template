package com.arplanets.template.dto.res;

import com.arplanets.template.enums.ResultMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserCreateResponse {

    @Schema(description = "帳號")
    private String email;

    @Schema(description = "新增結果")
    private String message;
}
