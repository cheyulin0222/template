package com.arplanets.template.controller;

import com.arplanets.template.casbin.CasbinAuthorize;
import com.arplanets.template.casbin.CasbinResource;
import com.arplanets.template.casbin.CasbinService;
import com.arplanets.template.enums.ResultMessage;
import com.arplanets.template.req.RoleCreateRequest;
import com.arplanets.template.req.RoleForUserAddRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/casbin")
@RequiredArgsConstructor
@CasbinResource("casbin")
@Tag(name = "授權")
public class CasbinController {

    private final CasbinService casbinService;

    @GetMapping(value = "/get-roles-by-user/{username}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "查詢使用者角色", security = @SecurityRequirement(name = "bearerAuth"))
    @CasbinAuthorize(action = "read")
    public ResponseEntity<List<String>> getRolesByUser(@PathVariable String username) {
        var result = casbinService.getRolesForUser(username);

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/add-role-for-user", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增使用者角色", security = @SecurityRequirement(name = "bearerAuth"))
    @CasbinAuthorize(action = "write")
    public ResponseEntity<ResultMessage> addRoleForUser(@Valid @RequestBody RoleForUserAddRequest request) {
        var result = casbinService.addRoleForUser(request.getUsername(), request.getRole());

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/create-role", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增角色", security = @SecurityRequirement(name = "bearerAuth"))
    @CasbinAuthorize(action = "write")
    public ResponseEntity<ResultMessage> createRole(@Valid @RequestBody RoleCreateRequest request) {
        var result = casbinService.createRole(request.getRole(), request.getResource(), request.getAction());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test-Header", "TestValue");

        // 返回帶有標頭的結果
        return ResponseEntity.ok()
                .headers(headers) // 設置響應標頭
                .body(result);

    }
}
