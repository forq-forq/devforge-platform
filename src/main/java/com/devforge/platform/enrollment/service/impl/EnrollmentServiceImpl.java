package com.devforge.platform.enrollment.service.impl;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.enrollment.repository.EnrollmentRepository;
import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

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
}