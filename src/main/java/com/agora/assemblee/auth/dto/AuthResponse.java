package com.agora.assemblee.auth.dto;

import java.util.Set;

public record AuthResponse(String accessToken, String refreshToken, String username, String email, Set<String> roles) {}
