package com.arplanets.template.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {

    @Schema(description = "帳號")
    @NotBlank
    private String email;

    @Schema(description = "密碼")
    @NotBlank
    private String password;
}
