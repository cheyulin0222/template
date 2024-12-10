package com.arplanet.template.req;

import lombok.Data;

@Data
public class RoleCreateRequest {

    private String role;

    private String resource;

    private String action;
}
