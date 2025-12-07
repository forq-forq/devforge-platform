package com.devforge.platform.user.domain;

/**
 * Defines the roles available in platform.
 * Used for authorization logic (RBAC).
 */
public enum Role {
    /**
     * Regular user who can enroll in courses and solve problems.
     */
    STUDENT,

    /**
     * Content creator who can manage their own courses.
     */
    TEACHER,

    /**
     * System administrator with full access to all resources.
     */
    ADMIN
}