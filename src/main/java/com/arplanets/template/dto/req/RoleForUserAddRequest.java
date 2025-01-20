package com.arplanets.template.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleForUserAddRequest {

    @Schema(description = "帳號")
    @NotBlank
    private String username;

    @Schema(description = "角色")
    @NotBlank
    private String role;
}
