package com.devforge.platform.course.service.impl;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.CourseStatus;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.service.CourseService;
import com.devforge.platform.course.web.dto.CreateCourseRequest;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public Course createCourse(CreateCourseRequest request, User author) {
        log.info("Creating new course '{}' by author {}", request.title(), author.getEmail());

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .level(request.level())
                .status(CourseStatus.DRAFT) // Always start as Draft
                .author(author)
                .build();

        return courseRepository.save(course);
    }

    @Override
    public List<Course> getAllPublishedCourses() {
        return courseRepository.findAllByStatus(CourseStatus.PUBLISHED);
    }

    @Override
    public List<Course> getCoursesByAuthor(User author) {
        return courseRepository.findAllByAuthorId(author.getId());
    }

    @Override
    @Transactional
    public void updateStatus(Long courseId, CourseStatus status, User actor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        // Only the author can change status
        if (!course.getAuthor().getId().equals(actor.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not the owner of this course");
        }

        course.setStatus(status);
        courseRepository.save(course);
        log.info("Course id={} status updated to {} by user {}", courseId, status, actor.getEmail());
    }

    @Override
    public Course getPublishedCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .filter(c -> c.getStatus() == CourseStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("Course not found or not available"));
    }
}