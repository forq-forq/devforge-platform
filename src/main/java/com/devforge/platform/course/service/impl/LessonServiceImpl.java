package com.devforge.platform.course.service.impl;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.course.service.LessonService;
import com.devforge.platform.course.web.dto.CreateLessonRequest;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Lesson createLesson(Long courseId, CreateLessonRequest request, User author) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Only author can add lessons
        if (!course.getAuthor().getId().equals(author.getId())) {
            throw new AccessDeniedException("You are not the author of this course");
        }

        Lesson lesson = Lesson.builder()
                .title(request.title())
                .content(request.content())
                .videoUrl(request.videoUrl())
                .orderIndex(request.orderIndex())
                .course(course)
                .build();

        log.info("Adding lesson '{}' to course id={}", request.title(), courseId);
        return lessonRepository.save(lesson);
    }

    @Override
    public List<Lesson> getLessonsByCourseId(Long courseId) {
        return lessonRepository.findAllByCourseIdOrderByOrderIndexAsc(courseId);
    }

    @Override
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + lessonId));
    }

    @Override
    @Transactional
    public void updateLecture(Long lessonId, CreateLessonRequest request, User user) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        // Check author
        if (!lesson.getCourse().getAuthor().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Not authorized");
        }

        // Update fields
        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setVideoUrl(request.videoUrl());
        lesson.setOrderIndex(request.orderIndex());
        
        lessonRepository.save(lesson);
    }
}