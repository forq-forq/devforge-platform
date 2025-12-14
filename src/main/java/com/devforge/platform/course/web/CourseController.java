package com.devforge.platform.course.web;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.course.web.dto.CreateCourseRequest;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import com.devforge.platform.course.service.LessonService;
import com.devforge.platform.course.web.dto.CreateLessonRequest;
import com.devforge.platform.practice.service.PracticeManagementService;
import com.devforge.platform.practice.web.dto.CreateProblemRequest;
import com.devforge.platform.quiz.service.QuizManagementService;
import com.devforge.platform.quiz.web.dto.CreateQuizRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
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
    private final LessonService lessonService;
    private final PracticeManagementService practiceManagementService;
    private final QuizManagementService quizManagementService;
    

    // CONSTANTS for views
    private static final String LIST_VIEW = "course/list";
    private static final String CREATE_VIEW = "course/create";
    private static final String MY_COURSES_VIEW = "course/my-courses";
    private static final String CREATE_LESSON_VIEW = "course/create-lesson";

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

    /**
     * Show form to add a lesson to a course.
     */
    @GetMapping("/{courseId}/lessons/create")
    @PreAuthorize("hasRole('TEACHER')")
    public String addLessonPage(@PathVariable Long courseId, 
                                @RequestParam(defaultValue = "LECTURE") String type,
                                Model model) {
        
        if ("PRACTICE".equals(type)) {
            // Create practice
            model.addAttribute("problem", new CreateProblemRequest()); 
            model.addAttribute("courseId", courseId);
            return "course/create-practice";
        } else if ("QUIZ".equals(type)) {
            // Create quiz
            model.addAttribute("quiz", new CreateQuizRequest());
            model.addAttribute("courseId", courseId);
            return "course/create-quiz";
        } else {
            // Create lecture
            var request = new CreateLessonRequest("", "", "", 1);
            model.addAttribute("lesson", request);
            model.addAttribute("courseId", courseId);
            return CREATE_LESSON_VIEW;
        }
    }

    /**
     * Process adding a lesson.
     */
    @PostMapping("/{courseId}/lessons")
    @PreAuthorize("hasRole('TEACHER')")
    public String addLessonProcess(@PathVariable Long courseId,
                                   @Valid @ModelAttribute("lesson") CreateLessonRequest request,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courseId", courseId);
            return CREATE_LESSON_VIEW;
        }

        User author = userService.getByEmail(principal.getName());
        try {
            lessonService.createLesson(courseId, request, author);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return "error/403";
        }

        return "redirect:/courses/my?lessonAdded";
    }

    /**
     * Choose a lesson datatype (LECTURE/PRACTICE/QUIZ)
     */
    @GetMapping("/{courseId}/lessons/choose-type")
    @PreAuthorize("hasRole('TEACHER')")
    public String chooseLessonType(@PathVariable Long courseId, Model model) {
        model.addAttribute("courseId", courseId);
        return "course/lesson-type-chooser";
    }

    /**
     * Crate practice lesson
     */
    @PostMapping("/{courseId}/lessons/create/practice")
    @PreAuthorize("hasRole('TEACHER')")
    public String createPracticeProcess(@PathVariable Long courseId,
                                        @ModelAttribute("problem") CreateProblemRequest request,
                                        Principal principal) {
        
        User teacher = userService.getByEmail(principal.getName());
        practiceManagementService.createPracticeLesson(courseId, request, teacher);
        
        return "redirect:/courses/my?practiceCreated";
    }

    /**
     * Create quiz
     */
    @PostMapping("/{courseId}/lessons/create/quiz")
    @PreAuthorize("hasRole('TEACHER')")
    public String createQuizProcess(@PathVariable Long courseId,
                                    @ModelAttribute("quiz") CreateQuizRequest request,
                                    Principal principal) {
        User teacher = userService.getByEmail(principal.getName());
        quizManagementService.createQuiz(courseId, request, teacher);
        return "redirect:/courses/my?quizCreated";
    }

    @GetMapping("/{id}")
    public String courseDetails(@PathVariable Long id, Model model, Principal principal) {
        Course course = courseService.getCourseById(id);
        
        // Check who observing
        boolean isAuthor = false;
        boolean isEnrolled = false;

         if (principal != null) {
            User user = userService.getByEmail(principal.getName());
            isAuthor = course.getAuthor().getId().equals(user.getId());
            
            // Check if student enrolled
            if (!isAuthor && user.getRole() == com.devforge.platform.user.domain.Role.STUDENT) {
                isEnrolled = enrollmentService.getStudentEnrollments(user).stream()
                        .anyMatch(e -> e.getCourse().getId().equals(id));
            }
        }

        // If course is in draft and not-author observing it -> 404
        if (course.getStatus() == com.devforge.platform.course.domain.CourseStatus.DRAFT && !isAuthor) {
            throw new org.springframework.security.access.AccessDeniedException("Course not published");
        }

        // Uppload lessons
        model.addAttribute("lessons", lessonService.getLessonsByCourseId(id)); 
        model.addAttribute("course", course);
        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isEnrolled", isEnrolled);
        
        return "course/details";
    }

    // UNPUBLISH Form
    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('TEACHER')")
    public String unpublishCourse(@PathVariable Long id, Principal principal) {
        User author = userService.getByEmail(principal.getName());
        courseService.updateStatus(id, com.devforge.platform.course.domain.CourseStatus.DRAFT, author);
        // Go to details page in case to start editing imediately.
        return "redirect:/courses/" + id; 
    }

    // EDIT Form
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String editCoursePage(@PathVariable Long id, Model model, Principal principal) {
        User author = userService.getByEmail(principal.getName());
        Course course = courseService.getCoursesByAuthor(author).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Access denied"));

        if (course.getStatus() != com.devforge.platform.course.domain.CourseStatus.DRAFT) {
            return "redirect:/courses/" + id + "?error=must_unpublish";
        }

        // Use DTO for creating, because the fields are same
        var request = new CreateCourseRequest(course.getTitle(), course.getDescription(), course.getLevel());
        
        model.addAttribute("course", request);
        model.addAttribute("courseId", id);
        return "course/edit";
    }

    // Process Edit
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String editCourseProcess(@PathVariable Long id,
                                    @Valid @ModelAttribute("course") CreateCourseRequest request,
                                    BindingResult bindingResult,
                                    @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                    Principal principal,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("courseId", id);
            return "course/edit";
        }

        User author = userService.getByEmail(principal.getName());
        try {
            courseService.updateCourseInfo(id, request, file, author);
        } catch (IllegalStateException e) {
            return "redirect:/courses/" + id + "?error=published";
        }
        
        return "redirect:/courses/" + id + "?updated";
    }
}