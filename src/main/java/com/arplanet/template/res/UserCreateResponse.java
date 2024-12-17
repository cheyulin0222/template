package com.arplanet.template.res;

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
}
