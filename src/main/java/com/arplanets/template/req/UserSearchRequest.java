package com.arplanets.template.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSearchRequest {

    @Schema(description = "帳號")
    @NotBlank
    private String email;

    @Schema(description = "起始年齡")
    @Min(1)
    @Max(120)
    private Integer ageStart;

    @Schema(description = "結束年齡")
    @Min(1)
    @Max(120)
    private Integer ageEnd;

    @Schema(description = "開始日期")
    @Min(1)
    @Max(120)
    private LocalDateTime createdAtStart;

    @Schema(description = "結束日期")
    @Min(1)
    @Max(120)
    private LocalDateTime createdAtEnd;
}
