package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinAuthorize;
import com.arplanet.template.casbin.CasbinResource;
import com.arplanet.template.casbin.CasbinService;
import com.arplanet.template.enums.ResultMessage;
import com.arplanet.template.req.RoleCreateRequest;
import com.arplanet.template.req.RoleForUserAddRequest;
import com.arplanet.template.res.ResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/casbin")
@RequiredArgsConstructor
@CasbinResource("casbin")
@Tag(name = "授權")
public class CasbinController {

    private final CasbinService casbinService;

    @GetMapping(value = "/get-roles-by-user/{username}")
    @CasbinAuthorize(action = "read")
    public ResponseModel<List<String>> getRolesByUser(@PathVariable String username) {
        List<String> result = casbinService.getRolesForUser(username);

        return new ResponseModel<>(result);
    }

    @PostMapping(value = "/add-role-for-user", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增使用者角色")
    @CasbinAuthorize(action = "write")
    public ResponseModel<ResultMessage> addRoleForUser(@Valid @RequestBody RoleForUserAddRequest request) {
        ResultMessage result = casbinService.addRoleForUser(request.getUsername(), request.getRole());

        return new ResponseModel<>(result);
    }

    @PostMapping(value = "/create-role", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增角色")
    @CasbinAuthorize(action = "write")
    public ResponseModel<ResultMessage> createRole(@Valid @RequestBody RoleCreateRequest request) {
        ResultMessage result = casbinService.createRole(request.getRole(), request.getResource(), request.getAction());
        return new ResponseModel<>(result);
    }
}
