package com.devforge.platform.review.repository;

import com.devforge.platform.review.domain.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    
    // Find all review for course (new first)
    List<CourseReview> findAllByCourseIdOrderByCreatedAtDesc(Long courseId);

    // Check if alreade reviewed course
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // Calculate average
    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.id = :courseId")
    Double getAverageRating(Long courseId);
}