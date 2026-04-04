package com.agora.assemblee.auth.controller;

import com.agora.assemblee.auth.dto.AuthResponse;
import com.agora.assemblee.auth.dto.LoginRequest;
import com.agora.assemblee.auth.service.AuthService;
import com.agora.assemblee.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request), "Connexion réussie");
    }
}
