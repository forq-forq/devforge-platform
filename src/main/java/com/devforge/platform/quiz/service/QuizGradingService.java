package com.devforge.platform.quiz.service;

import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.quiz.domain.QuizOption;
import com.devforge.platform.quiz.domain.QuizQuestion;
import com.devforge.platform.quiz.repository.QuizQuestionRepository;
import com.devforge.platform.quiz.web.dto.QuizSubmissionRequest;
import com.devforge.platform.quiz.web.dto.QuizSubmissionResult;
import com.devforge.platform.user.domain.Role;
import com.devforge.platform.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizGradingService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final EnrollmentService enrollmentService;

    private static final int PASS_THRESHOLD = 70; // passing threshold

    @Transactional
    public QuizSubmissionResult gradeQuiz(Long lessonId, QuizSubmissionRequest request, User user) {
        // Upload all the questions
        List<QuizQuestion> questions = quizQuestionRepository.findAllByLessonId(lessonId);
        
        if (questions.isEmpty()) {
            throw new IllegalStateException("Quiz has no questions");
        }

        int totalQuestions = questions.size();
        int correctCount = 0;
        Map<Long, Long> studentAnswers = request.answers();

        // Check answers
        for (QuizQuestion question : questions) {
            Long selectedOptionId = studentAnswers.get(question.getId());

            if (selectedOptionId != null) {
                boolean isCorrect = question.getOptions().stream()
                        .filter(opt -> opt.getId().equals(selectedOptionId))
                        .findFirst()
                        .map(QuizOption::isCorrect)
                        .orElse(false);

                if (isCorrect) {
                    correctCount++;
                }
            }
        }

        // Calculate percentage
        int scorePercent = (int) (((double) correctCount / totalQuestions) * 100);
        boolean passed = scorePercent >= PASS_THRESHOLD;

        // Return result
        if (passed) {
            if (user.getRole() == Role.STUDENT) {
                enrollmentService.markLessonAsComplete(user, questions.get(0).getLesson().getCourse().getId(), lessonId);
            }
            
            return new QuizSubmissionResult(true, scorePercent, "Great job! You passed with " + scorePercent + "%. 🏆");
        } else {
            return new QuizSubmissionResult(false, scorePercent, "You scored " + scorePercent + "%. You need " + PASS_THRESHOLD + "% to pass. Try again! ❌");
        }
    }
}