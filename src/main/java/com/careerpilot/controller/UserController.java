package com.careerpilot.controller;

import com.careerpilot.dto.request.AuthRequests;
import com.careerpilot.dto.request.UpdateProfileRequest;
import com.careerpilot.dto.response.Responses;
import com.careerpilot.entity.User;
import com.careerpilot.exception.ResourceNotFoundException;
import com.careerpilot.exception.UnauthorizedException;
import com.careerpilot.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public ResponseEntity<Responses.UserResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = findUser(userDetails.getUsername());
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<Responses.UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        User user = findUser(userDetails.getUsername());
        user.setFullName(request.fullName());
        userRepository.save(user);
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AuthRequests.ChangePasswordRequest request) {

        User user = findUser(userDetails.getUsername());

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private Responses.UserResponse toResponse(User user) {
        return new Responses.UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getCreatedAt()
        );
    }
}
