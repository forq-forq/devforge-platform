package com.devforge.platform.enrollment.repository;

import com.devforge.platform.enrollment.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Checks if a specific user is already enrolled in a specific course.
     */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Finds all enrollments for a specific student.
     * Used for the "My Learning" page.
     */
    List<Enrollment> findAllByUserId(Long userId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.author.id = :authorId")
    Long countTotalStudentsByAuthorId(Long authorId);
}