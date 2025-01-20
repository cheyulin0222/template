package com.arplanets.template.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationRequest {

    @Schema(description = "使用者名稱")
    @NotBlank
    private String username;

    @Schema(description = "密碼")
    @NotBlank
    private String password;
}
