package com.devforge.platform.enrollment.service;

import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.user.domain.User;
import java.util.List;

public interface EnrollmentService {

    /**
     * Enrolls a user in a specific course.
     * Checks validation rules (already enrolled, course status).
     */
    void enroll(Long courseId, User student);

    /**
     * Returns list of courses the student is currently learning.
     */
    List<Enrollment> getStudentEnrollments(User student);

    /**
     * Allow user to mark lessons that they're already complete
     */
    void markLessonAsComplete(User student, Long courseId, Long lessonId);

    /**
     * Returns a list of lessons that students already complete.
     */
    List<Long> getCompletedLessonIds(User student, Long courseId);
}