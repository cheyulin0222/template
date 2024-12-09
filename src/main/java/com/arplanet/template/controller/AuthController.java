package com.arplanet.template.controller;

import com.arplanet.template.casbin.CasbinService;
import com.arplanet.template.enums.OAuth2Type;
import com.arplanet.template.oauth2.OAuth2Provider;
import com.arplanet.template.req.LoginRequest;
import com.arplanet.template.req.OAuth2LoginRequest;
import com.arplanet.template.res.JwtResponse;
import com.arplanet.template.security.JwtAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "授權", description = "授權 API")
public class AuthController {

    private final Enforcer enforcer;
    private final ApplicationContext applicationContext;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthService jwtAuthService;
    private final CasbinService casbinService;

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "登入")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String username = authentication.getName();

        List<String> roles = casbinService.getRolesForUser(username);

        if (roles.isEmpty()) {
            casbinService.addRoleForUser(username, "user");
            roles = Collections.singletonList("user");
        }

        String jwt = jwtAuthService.generateToken(loginRequest.getUsername(), roles);

        return ResponseEntity.ok(JwtResponse.builder().token(jwt).build());
    }

    @PostMapping(value = "/oauth2/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "第三方登入")
    public ResponseEntity<JwtResponse> oauth2Login(
            @RequestParam OAuth2Type provider,
            @RequestBody OAuth2LoginRequest request) {

        OAuth2Provider oauth2Provider  = applicationContext.getBean(
                provider.getProviderBeanName(),
                OAuth2Provider.class
        );

        return ResponseEntity.ok(oauth2Provider.login(request));
    }
}
