package com.devforge.platform.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Represents a registered user in the system.
 * <p>
 * This entity implements {@link UserDetails} to integrate directly
 * with Spring Security authentication mechanisms.
 * </p>
 */
@Entity
@Table(name = "app_user") // 'user' is a reserved keyword in Postgres
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique email address, serves as the login username.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * BCrypt encoded password hash.
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String githubUrl;
    private String linkedinUrl;
    private String websiteUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer xp = 0;

    public String getAvatarUrl() {
        return "https://api.dicebear.com/7.x/notionists/svg?seed=" + (this.email != null ? this.email : "guest");
    }

    public int getLevel() {
        return 1 + (this.xp / 100);
    }

    /**
     * Automatically sets the creation timestamp before persisting.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // UserDetails Implementation

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    // For MVP, accounts never expire or lock
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}