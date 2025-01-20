package com.arplanets.template.dto.service.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchReqSO {

    private String email;

    private Integer age;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer ageStart;

    private Integer ageEnd;

    private LocalDateTime createdAtStart;

    private LocalDateTime createdAtEnd;

    private LocalDateTime updatedAtStart;

    private LocalDateTime updatedAtEnd;
}
