package com.devforge.platform.user.repository;

import com.devforge.platform.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data Access Layer for User entity.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email.
     * Useful for validation during registration.
     *
     * @param email the email to check
     * @return true if the email is already taken
     */
    boolean existsByEmail(String email);
}