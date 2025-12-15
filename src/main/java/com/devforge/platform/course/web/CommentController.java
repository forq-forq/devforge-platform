package com.devforge.platform.course.web;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devforge.platform.course.domain.LessonComment;
import com.devforge.platform.course.repository.LessonCommentRepository;
import com.devforge.platform.course.repository.LessonRepository;
import com.devforge.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final LessonCommentRepository commentRepository;
    private final LessonRepository lessonRepository;
    private final UserService userService;

    @PostMapping("/learn/{lessonId}/comments")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@PathVariable Long lessonId, 
                             @RequestParam String text, 
                             Principal principal) {
        
        var user = userService.getByEmail(principal.getName());
        var lesson = lessonRepository.findById(lessonId).orElseThrow();

        var comment = LessonComment.builder()
                .lesson(lesson)
                .user(user)
                .text(text)
                .build();
        
        commentRepository.save(comment);
        
        return "redirect:/learn/" + lesson.getCourse().getId() + "/lecture/" + lessonId + "#discussion";
    }
}