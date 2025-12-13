package com.devforge.platform.quiz.web.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateQuestionRequest {
    private String text;
    private List<CreateOptionRequest> options = new ArrayList<>();
}