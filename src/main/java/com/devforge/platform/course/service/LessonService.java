package com.devforge.platform.course.service;

import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.web.dto.CreateLessonRequest;
import com.devforge.platform.user.domain.User;

import java.util.List;

public interface LessonService {

    /**
     * Adds a lesson to an existing course.
     * Checks if the user is the author of the course.
     */
    Lesson createLesson(Long courseId, CreateLessonRequest request, User author);

    /**
     * Retrieves curriculum for a course.
     */
    List<Lesson> getLessonsByCourseId(Long courseId);

    /**
     * Retrieves a single lesson by ID.
     * @throws IllegalArgumentException if not found.
     */
    Lesson getLessonById(Long lessonId);
}