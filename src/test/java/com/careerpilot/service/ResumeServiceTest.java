package com.careerpilot.service;

import com.careerpilot.entity.Resume;
import com.careerpilot.entity.User;
import com.careerpilot.repository.ResumeRepository;
import com.careerpilot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        // Mock Security Context
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("test@example.com", "password");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getAllResumes_Success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        Resume resume = new Resume();
        resume.setId(UUID.randomUUID());
        resume.setUser(user);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findByUserIdOrderByUploadedAtDesc(user.getId())).thenReturn(java.util.List.of(resume));

        var response = resumeService.getAllResumes();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(resume.getId(), response.get(0).id());
    }

    @Test
    void getAllResumes_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> resumeService.getAllResumes());
    }
}
