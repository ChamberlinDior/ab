package com.agora.assemblee.auth.service;

import com.agora.assemblee.auth.dto.AuthResponse;
import com.agora.assemblee.auth.dto.LoginRequest;
import com.agora.assemblee.auth.model.User;
import com.agora.assemblee.auth.model.UserSession;
import com.agora.assemblee.auth.repository.UserRepository;
import com.agora.assemblee.auth.repository.UserSessionRepository;
import com.agora.assemblee.common.exception.BusinessException;
import com.agora.assemblee.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
        User user = userRepository.findByEmailIgnoreCase(request.usernameOrEmail())
                .or(() -> userRepository.findByUsernameIgnoreCase(request.usernameOrEmail()))
                .orElseThrow(() -> new BusinessException("Utilisateur invalide"));

        String accessToken = jwtService.generateToken(user, Map.of("roles", user.getRoles().stream().map(r -> r.getName()).toList()));
        String refreshToken = UUID.randomUUID().toString();

        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshToken(refreshToken);
        session.setDeviceName(request.deviceName());
        session.setExpiresAt(Instant.now().plusSeconds(86400));
        sessionRepository.save(session);

        return new AuthResponse(accessToken, refreshToken, user.getUsername(), user.getEmail(), roleNames(user));
    }

    @Transactional
    public void createDefaultAdminIfMissing() {
        if (userRepository.findByEmailIgnoreCase("admin@agora.ga").isPresent()) return;
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@agora.ga");
        admin.setPassword(passwordEncoder.encode("Admin@12345"));
        admin.setFullName("Administrateur AGORA");
        userRepository.save(admin);
    }

    private Set<String> roleNames(User user) {
        return user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
    }
}
