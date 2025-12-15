package com.devforge.platform.user.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;

/**
 * Data Access Layer for User entity.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Retrieves a user by their email address.
     *
     * @param egit add src/main/java/com/devforge/platform/user/web/dto/mail the email to search for
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

    List<User> findTop10ByRoleOrderByXpDesc(Role role);
}