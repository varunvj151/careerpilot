package com.careerpilot.service;

import com.careerpilot.dto.request.AuthRequests;
import com.careerpilot.dto.response.Responses;
import com.careerpilot.entity.User;
import com.careerpilot.exception.DuplicateResourceException;
import com.careerpilot.exception.ResourceNotFoundException;
import com.careerpilot.exception.UnauthorizedException;
import com.careerpilot.repository.UserRepository;
import com.careerpilot.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Responses.AuthResponse register(AuthRequests.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "An account with email " + request.email() + " already exists."
            );
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase().trim())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public Responses.AuthResponse login(AuthRequests.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().toLowerCase().trim(),
                        request.password()
                )
        );

        User user = (User) authentication.getPrincipal();
        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public Responses.AuthResponse refreshToken(AuthRequests.RefreshTokenRequest request) {
        String email;
        try {
            email = jwtService.extractUsername(request.refreshToken());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));

        if (!jwtService.isTokenValid(request.refreshToken(), user)) {
            throw new UnauthorizedException("Refresh token is invalid or expired.");
        }

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public Responses.UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return toUserResponse(user);
    }

    private Responses.AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new Responses.AuthResponse(
                accessToken,
                refreshToken,
                Responses.AuthResponse.TOKEN_TYPE,
                jwtService.getExpirationMs() / 1000,
                toUserResponse(user)
        );
    }

    private Responses.UserResponse toUserResponse(User user) {
        return new Responses.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCreatedAt()
        );
    }
}
