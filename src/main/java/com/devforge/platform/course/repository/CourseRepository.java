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

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' " +
            "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS String), '%'))) " +
            "AND (:level IS NULL OR c.level = :level)")
    List<Course> searchCourses(@org.springframework.data.repository.query.Param("keyword") String keyword, 
                               @org.springframework.data.repository.query.Param("level") com.devforge.platform.course.domain.CourseLevel level);
}