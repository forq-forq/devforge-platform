package com.devforge.platform.course.repository;

import com.devforge.platform.course.domain.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * Finds all lessons for a specific course, ordered by their sequence.
     * 
     * @param courseId ID of the course
     * @return List of ordered lessons
     */
    List<Lesson> findAllByCourseIdOrderByOrderIndexAsc(Long courseId);

    /**
     * Calculate the total number of lessons in course.
     * 
     * @param courseId ID of the course
     * @return long integer of total lessons in course.
     */
    long countByCourseId(long courseId);
}