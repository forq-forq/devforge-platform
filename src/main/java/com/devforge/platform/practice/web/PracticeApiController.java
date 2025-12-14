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
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<RunCodeResponse> runCode(@PathVariable Long lessonId,
                                                   @RequestBody RunCodeRequest request,
                                                   Principal principal) {
        
        User user = userService.getByEmail(principal.getName());
        
        Problem problem = problemRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("No problem found"));

        boolean passed = executionService.execute(request.code(), problem);

        if (passed) {
            if (user.getRole() == com.devforge.platform.user.domain.Role.STUDENT) {
                enrollmentService.markLessonAsComplete(user, problem.getLesson().getCourse().getId(), lessonId);
            }
            return ResponseEntity.ok(new RunCodeResponse(true, "All tests passed! 🏆"));
        } else {
            return ResponseEntity.ok(new RunCodeResponse(false, "Tests failed. ❌"));
        }
    }
}