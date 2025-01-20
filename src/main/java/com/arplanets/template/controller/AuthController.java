package com.arplanets.template.controller;

import com.arplanets.template.enums.OAuth2Type;
import com.arplanets.template.exception.TemplateApiException;
import com.arplanets.template.exception.RegistrationException;
import com.arplanets.template.log.Logger;
import com.arplanets.template.mapper.UserMapper;
import com.arplanets.template.oauth2.OAuth2Provider;
import com.arplanets.template.dto.req.AuthenticationRequest;
import com.arplanets.template.dto.req.OAuth2LoginRequest;
import com.arplanets.template.dto.res.JwtResponse;
import com.arplanets.template.security.JwtAuthService;
import com.arplanets.template.security.SecurityUser;
import com.arplanets.template.service.UserService;
import com.arplanets.template.service.impl.UserServiceImp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arplanets.template.exception.ErrorType.AUTH;
import static com.arplanets.template.exception.ErrorType.SYSTEM;
import static com.arplanets.template.log.enums.BasicActionType.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "驗證", description = "授權 API")
public class AuthController {

    private final ApplicationContext applicationContext;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthService jwtAuthService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "註冊")
    public ResponseEntity<JwtResponse> regitster(@Valid @RequestBody AuthenticationRequest request) {

        try {

            var user = userService.createUser(userMapper.authenticationRequestToUserCreateReqSO(request));

            List<String> roles = Collections.singletonList("user");

            String jwt = jwtAuthService.generateToken(user.getEmail(), roles);

            return ResponseEntity.ok(JwtResponse.builder().token(jwt).build());
        } catch (TemplateApiException e) {
            HashMap<String, Object> context = new HashMap<>();
            context.put("username", request.getUsername());
            Logger.error("Registration failed", REGISTER_USER, AUTH, context);
            throw new RegistrationException("Registration failed");
        } catch (Exception e) {
            Logger.error("System error during registration", REGISTER_USER, SYSTEM, e,
                    Map.of("username", request.getUsername()));
            throw new RuntimeException("System error occurred");
        }
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "登入")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthenticationRequest loginRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();


            String jwt = jwtAuthService.generateToken(loginRequest.getUsername(), null);

            return ResponseEntity.ok(JwtResponse.builder().token(jwt).build());

        } catch (BadCredentialsException e) {
            HashMap<String, Object> context = new HashMap<>();
            context.put("username", loginRequest.getUsername());
            Logger.error("Incorrect password", AUTHENTICATE_USER, AUTH, context);
            throw new BadCredentialsException("Invalid username or password");
        } catch (UsernameNotFoundException e) {
            HashMap<String, Object> context = new HashMap<>();
            context.put("username", loginRequest.getUsername());
            Logger.error("User not found", AUTHENTICATE_USER, AUTH, context);
            throw new BadCredentialsException("Invalid username or password");
        } catch (Exception e) {
            Logger.error("System error during registration", AUTHENTICATE_USER, SYSTEM, e,
                    Map.of("username", loginRequest.getUsername()));
            throw new RuntimeException("System error occurred");
        }
    }
}
