package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinAuthorize;
import com.arplanet.template.casbin.CasbinResource;
import com.arplanet.template.domain.User;
import com.arplanet.template.req.UserCreateRequest;
import com.arplanet.template.res.UserCreateResponse;
import com.arplanet.template.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CasbinResource("User")
@RequiredArgsConstructor
@Tag(name = "User (使用者) API")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "get-all", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "查詢所有使用者", security = @SecurityRequirement(name = "bearerAuth"))
    @CasbinAuthorize(action = "read")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @PostMapping(value = "/create-user", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增使用者", security = @SecurityRequirement(name = "bearerAuth"))
    @CasbinAuthorize(action = "write")
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody @Valid UserCreateRequest request) {

        var result = userService.createUser(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(result);
    }
}
