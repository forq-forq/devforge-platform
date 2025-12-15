package com.devforge.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Main security configuration for the application.
 * Defines authentication flow, password encoding, and access rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Define the password encoder bean.
     * BCrypt is the industry standard, so we use it.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the security filter chain.
     * Defines which URLs are public and which require authentication.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (Static resources)
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                // Public endpoints (Pages)
                .requestMatchers("/", "/register", "/login", "/error").permitAll()
                // H2 Console (Dev only)
                .requestMatchers("/h2-console/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page URL
                .usernameParameter("email") // We use email, not "username"
                .defaultSuccessUrl("/courses", true) // Redirect here after success
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // H2 Console specific settings (otherwise it won't work)
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/h2-console/**", 
                "/api/practice/**", 
                "/api/quiz/**",
                "/api/ai/**"
            ))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}