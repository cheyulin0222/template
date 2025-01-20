package com.arplanets.template.dto.service.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateResSO {

    private String email;

    private Integer age;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
