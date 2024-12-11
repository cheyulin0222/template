package com.arplanet.template.req;

import lombok.Data;

@Data
public class RoleForUserAddRequest {

    private String username;

    private String role;
}
