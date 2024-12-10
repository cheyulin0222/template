package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinService;
import com.arplanet.template.enums.ResultMessage;
import com.arplanet.template.req.RoleCreateRequest;
import com.arplanet.template.res.ResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.arplanet.template.enums.ResultMessage.INSERT_SUCCESSFUL;

@RestController
@RequestMapping("/api/casbin")
@RequiredArgsConstructor
@Tag(name = "授權")
public class CasbinController {

    private final CasbinService casbinService;

    public ResponseModel<List<String>> getRolesByUser(String username) {
        List<String> result = casbinService.getRolesForUser(username);

        return new ResponseModel<>(result);
    }

    @PostMapping(value = "/create-role", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增角色")
    public ResponseModel<ResultMessage> addRoleForUser(String username, String role) {
        ResultMessage result = casbinService.addRoleForUser(username, role);

        return new ResponseModel<>(result);
    }

    @PostMapping(value = "/create-role", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增角色")
    public ResponseModel<ResultMessage> createRole(@Valid @RequestBody RoleCreateRequest request) {
        ResultMessage result = casbinService.createRole(request.getRole(), request.getResource(), request.getAction());
        return new ResponseModel<>(result);
    }
}
