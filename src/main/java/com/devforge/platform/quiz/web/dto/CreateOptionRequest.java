package com.devforge.platform.quiz.web.dto;

import lombok.Data;

@Data
public class CreateOptionRequest {
    private String text;
    private boolean isCorrect;
}