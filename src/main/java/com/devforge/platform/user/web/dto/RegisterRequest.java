package com.devforge.platform.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration.
 * Uses Java Record for immutability.
 * Includes validation annotations.
 *
 * @param email must be a valid email format
 * @param password must be at least 6 characters
 * @param fullName required field
 * @param role optional role selection (defaulting to STUDENT logic in service)
 */
public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    @NotBlank(message = "Full name is required")
    String fullName,

    String role
) {}