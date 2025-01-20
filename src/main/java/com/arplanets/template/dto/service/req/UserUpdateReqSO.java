package com.arplanets.template.dto.service.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateReqSO {

    private String email;

    private String password;

    private int age;
}
