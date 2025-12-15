package com.devforge.platform.review.web;

import com.devforge.platform.review.service.ReviewService;
import com.devforge.platform.user.domain.User;
import com.devforge.platform.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping("/courses/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public String addReview(@PathVariable Long courseId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            Principal principal) {
        
        User student = userService.getByEmail(principal.getName());
        reviewService.addReview(courseId, student, rating, comment);
        
        return "redirect:/courses/" + courseId + "?reviewed";
    }
}