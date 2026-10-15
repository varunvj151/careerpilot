package com.careerpilot.service;

import com.careerpilot.dto.request.AuthRequests;
import com.careerpilot.dto.response.Responses;
import com.careerpilot.entity.User;
import com.careerpilot.exception.DuplicateResourceException;
import com.careerpilot.repository.UserRepository;
import com.careerpilot.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .password("encoded-pwd")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void register_Success() {
        AuthRequests.RegisterRequest request = new AuthRequests.RegisterRequest(
                "Test User",
                "test@example.com",
                "secret123"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-pwd");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("access-token-123");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token-123");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        Responses.AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-token-123", response.accessToken());
        assertEquals("refresh-token-123", response.refreshToken());
        assertEquals("test@example.com", response.user().email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ThrowsDuplicateResourceException() {
        AuthRequests.RegisterRequest request = new AuthRequests.RegisterRequest(
                "Test User",
                "test@example.com",
                "secret123"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        AuthRequests.LoginRequest request = new AuthRequests.LoginRequest(
                "test@example.com",
                "secret123"
        );

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(sampleUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtService.generateToken(sampleUser)).thenReturn("access-token-123");
        when(jwtService.generateRefreshToken(sampleUser)).thenReturn("refresh-token-123");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        Responses.AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token-123", response.accessToken());
        assertEquals("test@example.com", response.user().email());
    }
}
