package com.devforge.platform.practice.repository;

import com.devforge.platform.practice.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    /**
     * Finds the coding problem associated with a specific lesson.
     */
    Optional<Problem> findByLessonId(Long lessonId);
}