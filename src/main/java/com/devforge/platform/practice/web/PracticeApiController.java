package com.devforge.platform.practice.web;

import com.devforge.platform.enrollment.service.EnrollmentService;
import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.repository.ProblemRepository;
import com.devforge.platform.practice.service.CodeExecutionService;
import com.devforge.platform.practice.web.dto.RunCodeRequest;
import com.devforge.platform.practice.web.dto.RunCodeResponse;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
@Slf4j
public class PracticeApiController {

    private final CodeExecutionService executionService;
    private final ProblemRepository problemRepository;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @PostMapping("/{lessonId}/run")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RunCodeResponse> runCode(@PathVariable Long lessonId,
                                                   @RequestBody RunCodeRequest request,
                                                   Principal principal) {
        
        // 1. Fetch the problem
        Problem problem = problemRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("No problem found for this lesson"));

        // 2. Execute code
        boolean passed = executionService.execute(request.code(), problem);

        // 3. Handle result
        if (passed) {
            // Mark lesson as complete if tests pass
            User student = userService.getByEmail(principal.getName());
            enrollmentService.markLessonAsComplete(student, problem.getLesson().getCourse().getId(), lessonId);
            
            return ResponseEntity.ok(new RunCodeResponse(true, "All tests passed! Lesson completed. 🏆"));
        } else {
            return ResponseEntity.ok(new RunCodeResponse(false, "Tests failed. Check your logic and try again. ❌"));
        }
    }
}