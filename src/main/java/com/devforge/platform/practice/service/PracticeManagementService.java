package com.devforge.platform.practice.service;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.domain.TestCase;
import com.devforge.platform.practice.repository.ProblemRepository;
import com.devforge.platform.practice.web.dto.CreateProblemRequest;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PracticeManagementService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public void createPracticeLesson(Long courseId, CreateProblemRequest request, User teacher) {
        // Check the access rules
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        if (!course.getAuthor().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("You are not the author of this course");
        }

        // Create Lesson
        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(request.getOrderIndex())
                .type(LessonType.PRACTICE)
                .course(course)
                .build();
        
        lesson = lessonRepository.save(lesson);

        // Create Problem
        Problem problem = Problem.builder()
                .lesson(lesson)
                .className(request.getClassName())
                .methodName(request.getMethodName())
                .methodSignature(request.getMethodSignature())
                .starterCode(request.getStarterCode())
                .build();

        // Maps test cases (DTO -> Entity)
        Problem finalProblem = problem; // variable used in lambda must be final
        List<TestCase> testCases = request.getTestCases().stream()
                .map(dto -> TestCase.builder()
                        .problem(finalProblem)
                        .inputData(dto.getInputData())
                        .expectedOutput(dto.getExpectedOutput())
                        .build())
                .collect(Collectors.toList());
        
        problem.setTestCases(testCases);

        // Save problem
        problemRepository.save(problem);
        
        log.info("Created practice lesson '{}' with {} tests", lesson.getTitle(), testCases.size());
    }
}