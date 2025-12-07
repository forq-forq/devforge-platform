package com.devforge.platform.user.web;

import com.devforge.platform.user.service.UserService;
import com.devforge.platform.user.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller handling authentication related requests:
 * - Login page
 * - Registration page and form processing
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    // CONSTANTS to avoid duplication (SonarQube fix)
    private static final String REGISTER_VIEW = "auth/register";
    private static final String LOGIN_VIEW = "auth/login";

    /**
     * Shows the login page.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return LOGIN_VIEW; // returns templates/auth/login.html
    }

    /**
     * Shows the registration page.
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        // We pass an empty object to bind form data
        model.addAttribute("user", new RegisterRequest("", "", "", "STUDENT"));
        return REGISTER_VIEW; // returns templates/auth/register.html
    }

    /**
     * Processes the registration form.
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterRequest request,
                               BindingResult bindingResult,
                               Model model) {
        
        // 1. Check for validation errors (e.g. short password)
        if (bindingResult.hasErrors()) {
            return REGISTER_VIEW;
        }

        try {
            // 2. Call business logic
            userService.register(request);
            log.info("User registered successfully: {}", request.email());
            return "redirect:/login?success"; // Redirect to login
        } catch (IllegalArgumentException e) {
            // 3. Handle business errors (e.g. email taken)
            model.addAttribute("error", e.getMessage());
            return REGISTER_VIEW;
        }
    }
}