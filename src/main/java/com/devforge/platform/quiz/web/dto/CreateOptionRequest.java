package com.devforge.platform.quiz.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOptionRequest {
    private String text;
    private boolean isCorrect;
}