package com.arplanets.template.controller;

import com.arplanets.template.dto.service.req.UserCreateReqSO;
import com.arplanets.template.dto.service.req.UserSearchReqSO;
import com.arplanets.template.dto.service.req.UserUpdateReqSO;
import com.arplanets.template.mapper.UserMapper;
import com.arplanets.template.dto.req.UserCreateRequest;
import com.arplanets.template.dto.req.UserSearchRequest;
import com.arplanets.template.dto.req.UserUpdateRequest;
import com.arplanets.template.dto.res.UserCreateResponse;
import com.arplanets.template.dto.res.UserDeleteResponse;
import com.arplanets.template.dto.res.UserGetResponse;
import com.arplanets.template.dto.res.UserUpdateResponse;
import com.arplanets.template.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.arplanets.template.enums.ResultMessage.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users (使用者) API")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "查詢使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserGetResponse> getUser(
            @PathVariable
            @Email(message = "Invalid email format")
            String id
    ) {

        var result = userService.getUser(id);

        return ResponseEntity.ok(userMapper.userGetResSOToUserGetResponse(result));
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "查詢所有使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserGetResponse>> getUsers() {

        var result = userService.getAll();

        return ResponseEntity.ok(userMapper.userGetResSOToUserGetResponse(result));
    }

    @GetMapping(value = "/page", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "分頁查詢所有使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<UserGetResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "email") String[] sortBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortBy);
        var result = userService.getAllPaged(pageRequest)
                        .map(userMapper::userGetResSOToUserGetResponse);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "動態查詢所有使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<UserGetResponse>> getUsers(
            UserSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "email") String[] sortBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, direction, sortBy);

        UserSearchReqSO userSearchReqSO = userMapper.userSearchRequestToUserServiceDto(request);

        var result = userService.searchUser(userSearchReqSO, pageRequest)
                        .map(userMapper::userGetResSOToUserGetResponse);

        return ResponseEntity.ok(result);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "新增使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserCreateResponse> createUser(@RequestBody @Valid UserCreateRequest request) {

        UserCreateReqSO userCreateReqSO = userMapper.userCreateRequestToUserCreateReqSO(request);

        var result = userService.createUser(userCreateReqSO);

        return ResponseEntity.ok(userMapper.userCreateResSOToUserCreateResponse(result, CREATE_SUCCESSFUL));
    }

    @PutMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "修改使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserUpdateResponse> updateUser(@RequestBody @Valid UserUpdateRequest request) {

        UserUpdateReqSO userServiceDto = userMapper.userUpdateRequestToUserUpdateReqSO(request);

        var result = userService.updateUser(userServiceDto);

        return ResponseEntity.ok(userMapper.userUpdateResSOToUserUpdateResponse(result, UPDATE_SUCCESSFUL));
    }

    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "刪除使用者", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable String id) {

        userService.deleteUser(id);

        var result = UserDeleteResponse.builder()
                .message(DELETE_SUCCESSFUL.getMessage())
                .build();

        return ResponseEntity.ok(result);
    }
}
