package com.arplanets.template.controller;

import com.arplanets.template.dto.res.ServerInfoGetResponse;
import com.arplanets.template.service.ServerInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/server-info")
@RequiredArgsConstructor
@Tag(name = "服務器資訊", description = "服務器資訊 API")
public class ServerInfoController {

    private final ServerInfoService serverInfoService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "取得服務器資訊", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServerInfoGetResponse> getServerInfo() {

        var result = serverInfoService.getServerInfo();

        return ResponseEntity.ok(result);
    }
}
