package com.devforge.platform.practice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestCaseRequest {
    private String inputData;
    private String expectedOutput;
}