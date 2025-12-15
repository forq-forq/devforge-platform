package com.devforge.platform.practice.web;

import com.devforge.platform.common.service.GeminiService;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.practice.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiHelperController {

    private final GeminiService geminiService;
    private final LessonRepository lessonRepository;
    private final ProblemRepository problemRepository;

    @PostMapping("/summarize")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> summarize(@RequestBody Map<String, Long> payload) {
        var lesson = lessonRepository.findById(payload.get("lessonId")).orElseThrow();
        String result = geminiService.summarizeLecture(lesson.getContent());
        return ResponseEntity.ok(Map.of("text", result));
    }

    @PostMapping("/explain")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> explain(@RequestBody AiRequest req) {
        var problem = problemRepository.findByLessonId(req.lessonId).orElseThrow();
        String result = geminiService.explainError(problem.getLesson().getContent(), req.code, req.error);
        return ResponseEntity.ok(Map.of("text", result));
    }

    @PostMapping("/review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> review(@RequestBody AiRequest req) {
        var problem = problemRepository.findByLessonId(req.lessonId).orElseThrow();
        String result = geminiService.reviewCode(problem.getLesson().getContent(), req.code);
        return ResponseEntity.ok(Map.of("text", result));
    }

    // DTO for request
    public record AiRequest(Long lessonId, String code, String error) {}
}