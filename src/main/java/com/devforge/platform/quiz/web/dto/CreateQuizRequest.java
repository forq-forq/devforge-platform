package com.devforge.platform.quiz.web.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateQuizRequest {
    private String title;
    private Integer orderIndex;
    private List<CreateQuestionRequest> questions = new ArrayList<>();
}