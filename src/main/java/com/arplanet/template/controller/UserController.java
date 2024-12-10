package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinAuthorize;
import com.arplanet.template.casbin.CasbinResource;
import com.arplanet.template.domain.User;
import com.arplanet.template.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CasbinResource("User")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @CasbinAuthorize(action = "read")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @PostMapping
    @CasbinAuthorize(action = "write")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user.getEmail(), user.getPassword());
    }
}
