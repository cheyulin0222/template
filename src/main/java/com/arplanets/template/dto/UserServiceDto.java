package com.arplanets.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceDto {

    private String email;

    private String password;

    private int age;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserServiceAdvancedSearchDto advancedSearchInfo;


}
