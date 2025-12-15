package com.devforge.platform.user.web;

import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import com.devforge.platform.user.web.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String myProfile(Model model, Principal principal) {
        User user = userService.getByEmail(principal.getName());
        model.addAttribute("user", user);

        // Upload data depending on authority
        if (user.getRole() == com.devforge.platform.user.domain.Role.STUDENT) {
            model.addAttribute("enrollments", enrollmentService.getStudentEnrollments(user));
        } else if (user.getRole() == com.devforge.platform.user.domain.Role.TEACHER) {
            model.addAttribute("createdCourses", courseService.getCoursesByAuthor(user));
        }

        // Modify current field
        UpdateProfileRequest form = new UpdateProfileRequest();
        form.setFullName(user.getFullName());
        form.setBio(user.getBio());
        form.setGithubUrl(user.getGithubUrl());
        form.setLinkedinUrl(user.getLinkedinUrl());
        form.setWebsiteUrl(user.getWebsiteUrl());
        
        model.addAttribute("profileForm", form);

        return "user/profile";
    }

    @PostMapping("/profile/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@ModelAttribute("profileForm") UpdateProfileRequest request, 
                                Principal principal) {
        User user = userService.getByEmail(principal.getName());
        userService.updateProfile(user.getId(), request);
        return "redirect:/profile?updated";
    }
}