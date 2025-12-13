package com.devforge.platform.quiz.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.devforge.platform.quiz.domain.QuizQuestion;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findAllByLessonId(Long lessonId);
}