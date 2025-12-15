package com.devforge.platform.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devforge.platform.course.domain.LessonComment;

@Repository
public interface LessonCommentRepository extends JpaRepository<LessonComment, Long> {
    List<LessonComment> findAllByLessonIdOrderByCreatedAtDesc(Long lessonId);
}