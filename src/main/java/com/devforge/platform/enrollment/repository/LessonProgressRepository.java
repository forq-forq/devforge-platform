package com.devforge.platform.enrollment.repository;

import com.devforge.platform.enrollment.domain.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    
    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);

    List<LessonProgress> findAllByEnrollmentId(Long enrollmentId);
    
    long countByEnrollmentIdAndIsCompletedTrue(Long enrollmentId);
}