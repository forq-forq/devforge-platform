package com.devforge.platform.quiz.web;

import com.devforge.platform.quiz.service.QuizGradingService;
import com.devforge.platform.quiz.web.dto.QuizSubmissionRequest;
import com.devforge.platform.quiz.web.dto.QuizSubmissionResult;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizApiController {

    private final QuizGradingService quizGradingService;
    private final UserService userService;

    @PostMapping("/{lessonId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<QuizSubmissionResult> submitQuiz(@PathVariable Long lessonId,
                                                           @RequestBody QuizSubmissionRequest request,
                                                           Principal principal) {
        User student = userService.getByEmail(principal.getName());
        QuizSubmissionResult result = quizGradingService.gradeQuiz(lessonId, request, student);
        
        return ResponseEntity.ok(result);
    }
}