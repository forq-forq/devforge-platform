package com.devforge.platform.course.web;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.course.service.LessonService;
import com.devforge.platform.enrollment.repository.EnrollmentRepository;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.repository.ProblemRepository;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import com.devforge.platform.common.service.MarkdownService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/learn")
@RequiredArgsConstructor
@Slf4j
public class ClassroomController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final UserService userService;
    private final EnrollmentRepository enrollmentRepository; // Direct repo access for check (Clean enough for MVP)
    private final MarkdownService markdownService;
    private final EnrollmentService enrollmentService;
    private final ProblemRepository problemRepository;

    /**
     * Entry point for learning. Redirects to the first lesson of the course.
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String openClassroom(@PathVariable Long courseId, Principal principal) {
        User student = userService.getByEmail(principal.getName());

        // 1. Security: Check enrollment
        checkEnrollment(student, courseId);

        // 2. Find curriculum
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);

        if (lessons.isEmpty()) {
            // Edge case: Course has no lessons yet
            return "redirect:/my-learning?error=empty_course";
        }

        // 3. Redirect to the first lesson
        return "redirect:/learn/" + courseId + "/lecture/" + lessons.get(0).getId();
    }

    /**
     * Displays a specific lesson.
     */
    @GetMapping("/{courseId}/lecture/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String viewLesson(@PathVariable Long courseId, 
                             @PathVariable Long lessonId, 
                             Model model, 
                             Principal principal) {
        User student = userService.getByEmail(principal.getName());

        // 1. Security Check
        checkEnrollment(student, courseId);

        // 2. Load Data
        Course course = courseService.getPublishedCourseById(courseId);
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        Lesson currentLesson = lessonService.getLessonById(lessonId);

        // Validate that the lesson belongs to the course
        if (!currentLesson.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }

        // Render in markdown
        String htmlContent = markdownService.renderHtml(currentLesson.getContent());

        if (currentLesson.getType() == com.devforge.platform.course.domain.LessonType.PRACTICE) {
            Problem problem = problemRepository.findByLessonId(lessonId).orElse(null);
            model.addAttribute("problem", problem);
        }

        // 3. Populate Model
        model.addAttribute("course", course);
        model.addAttribute("lessons", lessons);       // For Sidebar
        model.addAttribute("currentLesson", currentLesson); // For Main Content
        model.addAttribute("htmlContent", htmlContent);
        
        // Find next/prev lesson IDs for navigation buttons
        int currentIndex = lessons.indexOf(currentLesson);
        if (currentIndex < lessons.size() - 1) {
            model.addAttribute("nextLessonId", lessons.get(currentIndex + 1).getId());
        }
        if (currentIndex > 0) {
            model.addAttribute("prevLessonId", lessons.get(currentIndex - 1).getId());
        }

        List<Long> completedLessonIds = enrollmentService.getCompletedLessonIds(student, courseId);
        model.addAttribute("completedLessonIds", completedLessonIds);

        return "course/classroom";
    }

    private void checkEnrollment(User student, Long courseId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(student.getId(), courseId)) {
            log.warn("Access denied: User {} is not enrolled in course {}", student.getEmail(), courseId);
            throw new AccessDeniedException("You must be enrolled to view this course.");
        }
    }

    @PostMapping("/{courseId}/lecture/{lessonId}/complete")
    @PreAuthorize("hasRole('STUDENT')")
    public String completeLesson(@PathVariable Long courseId, 
                                 @PathVariable Long lessonId, 
                                 Principal principal) {
        User student = userService.getByEmail(principal.getName());
        enrollmentService.markLessonAsComplete(student, courseId, lessonId);
        
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        Long nextLessonId = null;
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getId().equals(lessonId) && i < lessons.size() - 1) {
                nextLessonId = lessons.get(i + 1).getId();
                break;
            }
        }
        
        if (nextLessonId != null) {
            return "redirect:/learn/" + courseId + "/lecture/" + nextLessonId;
        } else {
            return "redirect:/my-learning?courseCompleted";
        }
    }
}