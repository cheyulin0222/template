package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinAuthorize;
import com.arplanet.template.casbin.CasbinResource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return userService.create(user);
    }
}
