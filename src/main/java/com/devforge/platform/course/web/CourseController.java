package com.devforge.platform.course.web;

import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.course.web.dto.CreateCourseRequest;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Controller for Course management pages.
 */
@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    // CONSTANTS for views
    private static final String LIST_VIEW = "course/list";
    private static final String CREATE_VIEW = "course/create";
    private static final String MY_COURSES_VIEW = "course/my-courses";


    /**
     * Lists all published courses (Public catalog).
     */
    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllPublishedCourses());
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

        // Fetch the currently logged-in user
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
    public String publishCourse(@org.springframework.web.bind.annotation.PathVariable Long id, Principal principal) {
        User author = userService.getByEmail(principal.getName());
        courseService.updateStatus(id, com.devforge.platform.course.domain.CourseStatus.PUBLISHED, author);
        return "redirect:/courses/my?published";
    }
}