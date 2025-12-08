package com.devforge.platform.course.service;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.web.dto.CreateCourseRequest;
import com.devforge.platform.user.domain.User;

import java.util.List;

/**
 * Service interface for Course management.
 */
public interface CourseService {

    /**
     * Creates a new course in DRAFT status.
     *
     * @param request Data from the form
     * @param author The currently logged-in user (Teacher)
     * @return The created Course
     */
    Course createCourse(CreateCourseRequest request, User author);

    /**
     * Returns all courses available for students (PUBLISHED only).
     */
    List<Course> getAllPublishedCourses();

    /**
     * Returns courses created by a specific teacher (DRAFT + PUBLISHED).
     */
    List<Course> getCoursesByAuthor(User author);

    /**
     * Updates the status of a course (e.g. DRAFT -> PUBLISHED).
     * Checks if the acting user is the owner of the course.
     */
    void updateStatus(Long courseId, com.devforge.platform.course.domain.CourseStatus status, User actor);

    /**
     * Retrieves a published course by its ID.
     * Used by Enrollment module to verify the course before joining.
     * @throws IllegalArgumentException if course not found or not published.
     */
    Course getPublishedCourseById(Long courseId);
}