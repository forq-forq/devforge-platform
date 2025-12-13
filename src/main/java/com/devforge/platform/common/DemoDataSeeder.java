package com.devforge.platform.common;

import com.devforge.platform.course.domain.*;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.domain.TestCase;
import com.devforge.platform.practice.repository.ProblemRepository;
import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ProblemRepository problemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // Не запускать, если база не пуста

        // 1. Users
        User teacher = User.builder()
                .email("teacher@devforge.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Dr. Java")
                .role(Role.TEACHER)
                .build();
        userRepository.save(teacher);

        User student = User.builder()
                .email("student@devforge.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Junior Dev")
                .role(Role.STUDENT)
                .build();
        userRepository.save(student);

        // 2. Course
        Course course = Course.builder()
                .title("Java Algorithms 101")
                .description("Master the basics of algorithms with Java.")
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.PUBLISHED)
                .author(teacher)
                .build();
        courseRepository.save(course);

        // 3. Lesson 1 (Lecture)
        Lesson lecture = Lesson.builder()
                .title("Welcome to the Course")
                .content("# Welcome!\nIn this course you will learn how to sum numbers using Java.")
                .course(course)
                .orderIndex(1)
                .type(LessonType.LECTURE)
                .build();
        lessonRepository.save(lecture);

        // 4. Lesson 2 (Practice)
        Lesson practiceLesson = Lesson.builder()
                .title("Problem: Sum of Two")
                .content("### Task\nImplement the method `sum` which takes two integers and returns their sum.")
                .course(course)
                .orderIndex(2)
                .type(LessonType.PRACTICE)
                .build();
        lessonRepository.save(practiceLesson);

        // 5. The Problem (Coding Task)
        Problem problem = Problem.builder()
                .lesson(practiceLesson)
                .className("Solution")
                .methodName("sum")
                .methodSignature("int, int")
                .starterCode("public class Solution {\n    public int sum(int a, int b) {\n        // TODO: return the sum of a and b\n        return 0;\n    }\n}")
                .build();
        problemRepository.save(problem);

        // 6. Test Cases
        List<TestCase> tests = List.of(
            TestCase.builder().problem(problem).inputData("5, 10").expectedOutput("15").build(),
            TestCase.builder().problem(problem).inputData("-1, 1").expectedOutput("0").build(),
            TestCase.builder().problem(problem).inputData("100, 200").expectedOutput("300").build()
        );
        problem.setTestCases(tests);
        problemRepository.save(problem); // Cascade save tests

        System.out.println("✅ DEMO DATA LOADED SUCCESSFULLY");
    }
}