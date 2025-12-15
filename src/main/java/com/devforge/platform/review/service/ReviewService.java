package com.devforge.platform.review.service;

import com.devforge.platform.enrollment.domain.Enrollment;
import com.devforge.platform.enrollment.repository.EnrollmentRepository;
import com.devforge.platform.review.domain.CourseReview;
import com.devforge.platform.review.repository.CourseReviewRepository;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CourseReviewRepository reviewRepository;
    private final EnrollmentRepository enrollmentRepository; // Check the progress

    @Transactional
    public void addReview(Long courseId, User student, Integer rating, String comment) {
        // Check if enrolled and completed the course
        Enrollment enrollment = enrollmentRepository.findAllByUserId(student.getId()).stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not enrolled in this course"));

        if (enrollment.getProgress() < 100) {
            throw new AccessDeniedException("You must complete the course to leave a review.");
        }

        // Check if already reviewed
        if (reviewRepository.existsByUserIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("You have already reviewed this course.");
        }

        // Save
        CourseReview review = CourseReview.builder()
                .course(enrollment.getCourse())
                .student(student)
                .rating(rating)
                .comment(comment)
                .build();
        
        reviewRepository.save(review);
    }

    public List<CourseReview> getReviewsForCourse(Long courseId) {
        return reviewRepository.findAllByCourseIdOrderByCreatedAtDesc(courseId);
    }

    public Double getAverageRating(Long courseId) {
        Double avg = reviewRepository.getAverageRating(courseId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0; // Approx to 1 sign (4.5)
    }
}