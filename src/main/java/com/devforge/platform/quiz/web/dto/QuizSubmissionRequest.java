package com.devforge.platform.quiz.web.dto;

import java.util.Map;

public record QuizSubmissionRequest(
    // Map<QuestionID, OptionID>
    Map<Long, Long> answers
) {}