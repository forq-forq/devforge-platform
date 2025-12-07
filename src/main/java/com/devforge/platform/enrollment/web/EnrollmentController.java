package com.devforge.platform.enrollment.web;

import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

/**
 * Controller for student enrollment actions.
 */
@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    private static final String MY_LEARNING_VIEW = "enrollment/my";

    /**
     * Handles the "Start Course" action.
     * Redirects to the My Learning dashboard upon success.
     */
    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("isAuthenticated()") // Any logged-in user can enroll
    public String enrollInCourse(@PathVariable Long courseId, Principal principal) {
        User student = userService.getByEmail(principal.getName());
        enrollmentService.enroll(courseId, student);
        
        return "redirect:/my-learning?enrolled";
    }

    /**
     * Displays the list of courses the student is enrolled in.
     */
    @GetMapping("/my-learning")
    @PreAuthorize("isAuthenticated()")
    public String myLearningPage(Model model, Principal principal) {
        User student = userService.getByEmail(principal.getName());
        model.addAttribute("enrollments", enrollmentService.getStudentEnrollments(student));
        return MY_LEARNING_VIEW;
    }
}