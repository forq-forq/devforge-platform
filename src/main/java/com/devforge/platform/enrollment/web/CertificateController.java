package com.devforge.platform.enrollment.web;

import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.enrollment.repository.EnrollmentRepository;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class CertificateController {

    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    @GetMapping("/certificate/{enrollmentId}")
    @PreAuthorize("isAuthenticated()")
    public String getCertificate(@PathVariable Long enrollmentId, Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        // Check the authority
        if (!enrollment.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This certificate does not belong to you.");
        }

        // Check if the student complete the course
        if (enrollment.getProgress() < 100) {
            throw new AccessDeniedException("Course not completed yet! Keep learning.");
        }

        model.addAttribute("enrollment", enrollment);
        model.addAttribute("student", user);
        model.addAttribute("course", enrollment.getCourse());
        model.addAttribute("date", java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        return "enrollment/certificate";
    }
}