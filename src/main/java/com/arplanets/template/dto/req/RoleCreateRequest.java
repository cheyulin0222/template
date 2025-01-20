package com.arplanets.template.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleCreateRequest {

    @Schema(description = "角色")
    @NotBlank
    private String role;

    @Schema(description = "資源")
    @NotBlank
    private String resource;

    @Schema(description = "動作")
    @NotBlank
    private String action;
}
