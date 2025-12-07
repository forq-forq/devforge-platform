package com.devforge.platform.user.service;

import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.web.dto.RegisterRequest;

/**
 * Business Logic Interface for User management.
 * Decouples the controller from the actual implementation logic.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param request the DTO containing registration data
     * @return the created User entity
     * @throws IllegalArgumentException if the email is already in use
     */
    User register(RegisterRequest request);

    /**
     * Finds a user by email.
     * @param email The email to search for.
     * @return The User entity.
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if not found.
     */
    User getByEmail(String email);
}