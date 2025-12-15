package com.devforge.platform.enrollment.service.impl;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.enrollment.domain.EnrollmentStatus;
import com.devforge.platform.enrollment.domain.LessonProgress;
import com.devforge.platform.enrollment.repository.EnrollmentRepository;
import com.devforge.platform.enrollment.repository.LessonProgressRepository;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;
    private final com.devforge.platform.user.service.GamificationService gamificationService;

    @Override
    @Transactional
    public void enroll(Long courseId, User student) {
        // 1. Check if already enrolled (Idempotency)
        if (enrollmentRepository.existsByUserIdAndCourseId(student.getId(), courseId)) {
            log.warn("User {} tried to enroll again in course {}", student.getEmail(), courseId);
            return; // Or throw exception, but silent return is often better for UX here
        }

        // 2. Get the course (Logic inside CourseService ensures it is PUBLISHED)
        Course course = courseService.getPublishedCourseById(courseId);

        // 3. Create record
        Enrollment enrollment = Enrollment.builder()
                .user(student)
                .course(course)
                .build(); // status ACTIVE, progress 0 set by @PrePersist

        enrollmentRepository.save(enrollment);
        log.info("Student {} enrolled in course '{}'", student.getEmail(), course.getTitle());
    }

    @Override
    public List<Enrollment> getStudentEnrollments(User student) {
        return enrollmentRepository.findAllByUserId(student.getId());
    }

    @Override
    @Transactional
    public void markLessonAsComplete(User student, Long courseId, Long lessonId) {
        Enrollment enrollment = enrollmentRepository.findAllByUserId(student.getId()).stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Student not enrolled in this course"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        var existingProgress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollment.getId(), lessonId);

        if (existingProgress.isPresent() && existingProgress.get().isCompleted()) {
            return;
        }
        
        if (!lesson.getCourse().getId().equals(courseId)) {
             throw new IllegalArgumentException("Lesson belongs to another course");
        }

        LessonProgress progress = existingProgress.orElse(LessonProgress.builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .build());

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(progress);

        gamificationService.awardXp(student, lesson.getType());
        
        long totalLessons = lessonRepository.countByCourseId(courseId); // Надо добавить этот метод в LessonRepository!
        long completedLessons = lessonProgressRepository.countByEnrollmentIdAndIsCompletedTrue(enrollment.getId());

        int percent = (int) ((completedLessons * 100) / totalLessons);
        
        enrollment.setProgress(percent);
        if (percent == 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Long> getCompletedLessonIds(User student, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findAllByUserId(student.getId()).stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElse(null);
        
        if (enrollment == null) return List.of();

        return lessonProgressRepository.findAllByEnrollmentId(enrollment.getId()).stream()
                .filter(LessonProgress::isCompleted)
                .map(lp -> lp.getLesson().getId())
                .toList();
    }
}