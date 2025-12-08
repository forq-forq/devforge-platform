package com.devforge.platform.course.web;

import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.course.web.dto.CreateCourseRequest;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for Course management pages.
 * Handles listing, creation, and publishing of courses.
 */
@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final EnrollmentService enrollmentService; 

    // CONSTANTS for views
    private static final String LIST_VIEW = "course/list";
    private static final String CREATE_VIEW = "course/create";
    private static final String MY_COURSES_VIEW = "course/my-courses";

    /**
     * Lists all published courses (Public catalog).
     * Calculates which courses the current user is already enrolled in.
     */
    @GetMapping
    public String listCourses(Model model, Principal principal) {
        model.addAttribute("courses", courseService.getAllPublishedCourses());

        Set<Long> enrolledCourseIds = Collections.emptySet();

        if (principal != null) {
            User user = userService.getByEmail(principal.getName());
            // Only fetch enrollments if user is a STUDENT
            if (user.getRole() == com.devforge.platform.user.domain.Role.STUDENT) {
                enrolledCourseIds = enrollmentService.getStudentEnrollments(user).stream()
                        .map(enrollment -> enrollment.getCourse().getId())
                        .collect(Collectors.toSet());
            }
        }
        
        model.addAttribute("enrolledCourseIds", enrolledCourseIds);
        return LIST_VIEW;
    }

    /**
     * Shows the form to create a new course.
     * Only accessible by TEACHER or ADMIN.
     */
    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String createCoursePage(Model model) {
        model.addAttribute("course", new CreateCourseRequest("", "", null));
        return CREATE_VIEW;
    }

    /**
     * Handles the creation form submission.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public String createCourseProcess(@Valid @ModelAttribute("course") CreateCourseRequest request,
                                      BindingResult bindingResult,
                                      Principal principal) {
        if (bindingResult.hasErrors()) {
            return CREATE_VIEW;
        }

        User author = userService.getByEmail(principal.getName());
        courseService.createCourse(request, author);
        
        return "redirect:/courses?created";
    }

    /**
     * Lists courses created by the logged-in teacher.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('TEACHER')")
    public String myCourses(Model model, Principal principal) {
        User author = userService.getByEmail(principal.getName());
        model.addAttribute("courses", courseService.getCoursesByAuthor(author));
        return MY_COURSES_VIEW;
    }

    /**
     * Publishes a draft course.
     */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('TEACHER')")
    public String publishCourse(@PathVariable Long id, Principal principal) {
        User author = userService.getByEmail(principal.getName());
        courseService.updateStatus(id, com.devforge.platform.course.domain.CourseStatus.PUBLISHED, author);
        return "redirect:/courses/my?published";
    }
}