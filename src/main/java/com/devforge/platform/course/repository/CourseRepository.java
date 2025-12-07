package com.devforge.platform.course.repository;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data Access Layer for Course entity.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds all courses with a specific status (e.g., PUBLISHED).
     * Used for the public course catalog.
     */
    List<Course> findAllByStatus(CourseStatus status);

    /**
     * Finds all courses created by a specific teacher.
     * Used for the teacher's dashboard.
     */
    List<Course> findAllByAuthorId(Long authorId);
}