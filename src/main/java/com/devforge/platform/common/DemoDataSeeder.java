package com.devforge.platform.common;

import com.devforge.platform.course.domain.*;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.domain.TestCase;
import com.devforge.platform.practice.repository.ProblemRepository;
import com.devforge.platform.quiz.domain.QuizOption;
import com.devforge.platform.quiz.domain.QuizQuestion;
import com.devforge.platform.quiz.repository.QuizQuestionRepository;
import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ProblemRepository problemRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Данные уже есть, не перезаписываем
        }

        // --- 1. USERS ---
        User teacher = User.builder()
                .email("teacher@devforge.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Dr. Gosling")
                .role(Role.TEACHER)
                .build();
        userRepository.save(teacher);

        User student = User.builder()
                .email("student@devforge.com")
                .password(passwordEncoder.encode("password"))
                .fullName("Java Padawan")
                .role(Role.STUDENT)
                .build();
        userRepository.save(student);

        // --- 2. COURSE ---
        Course course = Course.builder()
                .title("Java Algorithms: The Beginning")
                .description("A gentle introduction to algorithmic thinking using Java. We will cover variables, math operations, and conditional logic.")
                .level(CourseLevel.BEGINNER)
                .status(CourseStatus.PUBLISHED)
                .author(teacher)
                .build();
        courseRepository.save(course);

        // --- 3. LESSONS ---

        // ==========================================
        // LESSON 1: LECTURE (Intro)
        // ==========================================
        Lesson l1 = Lesson.builder()
                .title("1. What is an Algorithm?")
                .orderIndex(1)
                .type(LessonType.LECTURE)
                .course(course)
                .content("""
                        # Introduction to Algorithms
                        
                        An **algorithm** is simply a set of steps to accomplish a task. Think of it like a recipe for cooking.
                        
                        ### Key Properties:
                        1. **Input**: Ingredients you need.
                        2. **Process**: Steps to follow.
                        3. **Output**: The delicious meal!
                        
                        In Java, we express these steps using code.
                        """)
                .videoUrl("https://www.youtube.com/embed/kPRA0W1kECg") // Dummy video (Algorithms intro)
                .build();
        lessonRepository.save(l1);

        // ==========================================
        // LESSON 2: QUIZ (Check Theory)
        // ==========================================
        Lesson l2 = Lesson.builder()
                .title("2. Knowledge Check")
                .orderIndex(2)
                .type(LessonType.QUIZ)
                .course(course)
                .build();
        lessonRepository.save(l2);

        QuizQuestion q1 = QuizQuestion.builder().text("What is an algorithm?").lesson(l2).build();
        q1.setOptions(List.of(
                QuizOption.builder().text("A type of musical instrument").isCorrect(false).question(q1).build(),
                QuizOption.builder().text("A step-by-step instruction to solve a problem").isCorrect(true).question(q1).build(),
                QuizOption.builder().text("A database for storing videos").isCorrect(false).question(q1).build()
        ));
        quizQuestionRepository.save(q1);

        QuizQuestion q2 = QuizQuestion.builder().text("Which of these is a valid Java variable type for whole numbers?").lesson(l2).build();
        q2.setOptions(List.of(
                QuizOption.builder().text("int").isCorrect(true).question(q2).build(),
                QuizOption.builder().text("text").isCorrect(false).question(q2).build(),
                QuizOption.builder().text("decimal").isCorrect(false).question(q2).build()
        ));
        quizQuestionRepository.save(q2);

        // ==========================================
        // LESSON 3: PRACTICE (Simple Math)
        // ==========================================
        Lesson l3 = Lesson.builder()
                .title("3. Practice: Simple Addition")
                .orderIndex(3)
                .type(LessonType.PRACTICE)
                .course(course)
                .content("""
                        ### Task: Sum of Two Numbers
                        
                        Write a method that takes two integers `a` and `b` and returns their sum.
                        
                        **Example:**
                        * Input: `2, 3`
                        * Output: `5`
                        """)
                .build();
        lessonRepository.save(l3);

        Problem p1 = Problem.builder()
                .lesson(l3)
                .className("Solution")
                .methodName("sum")
                .methodSignature("int, int")
                .starterCode("""
                        public class Solution {
                            public int sum(int a, int b) {
                                // TODO: return the sum of a and b
                                return 0;
                            }
                        }
                        """)
                .build();
        p1.setTestCases(List.of(
                TestCase.builder().problem(p1).inputData("5, 10").expectedOutput("15").build(),
                TestCase.builder().problem(p1).inputData("-5, 5").expectedOutput("0").build(),
                TestCase.builder().problem(p1).inputData("100, 200").expectedOutput("300").build()
        ));
        problemRepository.save(p1);

        // ==========================================
        // LESSON 4: LECTURE (Conditionals)
        // ==========================================
        Lesson l4 = Lesson.builder()
                .title("4. Making Decisions (If/Else)")
                .orderIndex(4)
                .type(LessonType.LECTURE)
                .course(course)
                .content("""
                        # If / Else Logic
                        
                        Sometimes your code needs to make a choice. We use `if` statements for this.
                        
                        ```java
                        if (age >= 18) {
                            System.out.println("Adult");
                        } else {
                            System.out.println("Minor");
                        }
                        ```
                        
                        ### Comparison Operators:
                        * `>` Greater than
                        * `<` Less than
                        * `==` Equal to
                        """)
                .build();
        lessonRepository.save(l4);

        // ==========================================
        // LESSON 5: PRACTICE (Logic)
        // ==========================================
        Lesson l5 = Lesson.builder()
                .title("5. Practice: Find Maximum")
                .orderIndex(5)
                .type(LessonType.PRACTICE)
                .course(course)
                .content("""
                        ### Task: Find the Maximum
                        
                        Implement the method `max` that takes two integers and returns the larger one.
                        
                        **Do not use Math.max()**, try to use `if/else` logic!
                        """)
                .build();
        lessonRepository.save(l5);

        Problem p2 = Problem.builder()
                .lesson(l5)
                .className("Solution")
                .methodName("max")
                .methodSignature("int, int")
                .starterCode("""
                        public class Solution {
                            public int max(int a, int b) {
                                // TODO: return the larger number
                                return 0;
                            }
                        }
                        """)
                .build();
        p2.setTestCases(List.of(
                TestCase.builder().problem(p2).inputData("10, 20").expectedOutput("20").build(),
                TestCase.builder().problem(p2).inputData("50, 10").expectedOutput("50").build(),
                TestCase.builder().problem(p2).inputData("7, 7").expectedOutput("7").build()
        ));
        problemRepository.save(p2);

        // ==========================================
        // LESSON 6: PRACTICE (String Logic)
        // ==========================================
        Lesson l6 = Lesson.builder()
                .title("6. Practice: Hello You")
                .orderIndex(6)
                .type(LessonType.PRACTICE)
                .course(course)
                .content("""
                        ### Task: Greeting
                        
                        Write a method that takes a name (String) and returns a greeting "Hello, [Name]!".
                        
                        **Example:**
                        * Input: `"Alice"`
                        * Output: `"Hello, Alice!"`
                        """)
                .build();
        lessonRepository.save(l6);

        Problem p3 = Problem.builder()
                .lesson(l6)
                .className("Solution")
                .methodName("greet")
                .methodSignature("String")
                .starterCode("""
                        public class Solution {
                            public String greet(String name) {
                                // Hint: Use + to concatenate strings
                                return "";
                            }
                        }
                        """)
                .build();
        p3.setTestCases(List.of(
                TestCase.builder().problem(p3).inputData("Alice").expectedOutput("Hello, Alice!").build(),
                TestCase.builder().problem(p3).inputData("Bob").expectedOutput("Hello, Bob!").build(),
                TestCase.builder().problem(p3).inputData("Java").expectedOutput("Hello, Java!").build()
        ));
        problemRepository.save(p3);

        System.out.println("✅ FULL DEMO COURSE LOADED");
    }
}