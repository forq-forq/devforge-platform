package com.devforge.platform.quiz.service;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.course.domain.Lesson;
import com.devforge.platform.course.domain.LessonType;
import com.devforge.platform.course.repository.CourseRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.quiz.domain.QuizOption;
import com.devforge.platform.quiz.domain.QuizQuestion;
import com.devforge.platform.quiz.repository.QuizQuestionRepository;
import com.devforge.platform.quiz.web.dto.CreateQuizRequest;
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
public class QuizManagementService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @Transactional
    public void createQuiz(Long courseId, CreateQuizRequest request, User teacher) {
        // Check access rules
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        
        if (!course.getAuthor().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Not authorized");
        }

        // Create Lesson
        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .type(LessonType.QUIZ)
                .course(course)
                .build();
        
        lessonRepository.save(lesson);

        // Save questions with answers
        for (var qDto : request.getQuestions()) {
            // Create question
            QuizQuestion question = QuizQuestion.builder()
                    .text(qDto.getText())
                    .lesson(lesson)
                    .build();
            
            // Create answers for this question
            List<QuizOption> options = qDto.getOptions().stream()
                    .map(oDto -> QuizOption.builder()
                            .text(oDto.getText())
                            .isCorrect(oDto.isCorrect())
                            .question(question)
                            .build())
                    .collect(Collectors.toList());
            
            question.setOptions(options);
            
            // Save question
            quizQuestionRepository.save(question);
        }

        log.info("Quiz '{}' created with {} questions", lesson.getTitle(), request.getQuestions().size());
    }

    @Transactional
    public void updateQuiz(Long lessonId, CreateQuizRequest request, User teacher) {
        // Find lesson
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
        
        if (!lesson.getCourse().getAuthor().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Not authorized");
        }

        // Update basic information
        lesson.setTitle(request.getTitle());
        lesson.setOrderIndex(request.getOrderIndex());
        lessonRepository.save(lesson);

        // Update questions
        List<QuizQuestion> oldQuestions = quizQuestionRepository.findAllByLessonId(lessonId);
        quizQuestionRepository.deleteAll(oldQuestions);
        quizQuestionRepository.flush(); 

        // Create again
        for (var qDto : request.getQuestions()) {
            QuizQuestion question = QuizQuestion.builder()
                    .text(qDto.getText())
                    .lesson(lesson)
                    .build();
            
            List<QuizOption> options = qDto.getOptions().stream()
                    .map(oDto -> QuizOption.builder()
                            .text(oDto.getText())
                            .isCorrect(oDto.isCorrect())
                            .question(question)
                            .build())
                    .collect(Collectors.toList());
            
            question.setOptions(options);
            quizQuestionRepository.save(question);
        }
    }
}