package com.devforge.platform.quiz.web.dto;

public record QuizSubmissionResult(
    boolean passed,
    int scorePercent,
    String message
) {}