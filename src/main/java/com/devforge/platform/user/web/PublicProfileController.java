package com.devforge.platform.user.web;

import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PublicProfileController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    // Public URL
    @GetMapping("/u/{userId}")
    public String userProfile(@PathVariable Long userId, Model model) {
        // Find user
        User user = userService.getById(userId); 

        model.addAttribute("profileUser", user);

        if (user.getRole() == Role.TEACHER) {
            // Show the courses (published)
            var courses = courseService.getCoursesByAuthor(user).stream()
                    .filter(c -> c.getStatus() == com.devforge.platform.course.domain.CourseStatus.PUBLISHED)
                    .collect(Collectors.toList());
            model.addAttribute("courses", courses);
            model.addAttribute("isTeacher", true);
        } else {
            // Show the achievments of student
            List<Enrollment> certificates = enrollmentService.getStudentEnrollments(user).stream()
                    .filter(e -> e.getProgress() == 100)
                    .collect(Collectors.toList());
            model.addAttribute("certificates", certificates);
            model.addAttribute("isTeacher", false);
        }

        return "user/public-profile";
    }
}