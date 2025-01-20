package com.arplanets.template.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResponse {

    @Schema(description = "帳號")
    private String email;

    @Schema(description = "年齡")
    private Integer age;

    @Schema(description = "新增日期")
    private LocalDateTime createdAt;

    @Schema(description = "修改日期")
    private LocalDateTime updatedAt;


}
