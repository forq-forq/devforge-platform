package com.devforge.platform.user.service.impl;

import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.repository.UserRepository;
import com.devforge.platform.user.service.UserService;
import com.devforge.platform.user.web.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserService.
 * Contains the actual business logic for user management.
 */
@Service
@RequiredArgsConstructor
@Slf4j // Adds a logger automatically
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     * 1. Checks if email is taken.
     * 2. Resolves the role (defaults to STUDENT).
     * 3. Hashes the password using BCrypt.
     * 4. Saves to database.
     */
    @Override
    @Transactional
    public User register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.email());

        // 1. Validation rule: Email must be unique
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email {} already in use", request.email());
            throw new IllegalArgumentException("User with this email already exists");
        }

        // 2. Role resolution (Safe default)
        Role role = Role.STUDENT;
        if (request.role() != null && !request.role().isBlank()) {
            try {
                role = Role.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role '{}' provided, defaulting to STUDENT", request.role());
            }
        }

        // 3. Entity construction with Password Hashing
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Critical security step!
                .fullName(request.fullName())
                .role(role)
                .build();

        // 4. Persistence
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found: " + email));
    }
}