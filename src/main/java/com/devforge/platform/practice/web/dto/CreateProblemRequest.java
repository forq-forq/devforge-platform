package com.devforge.platform.practice.web.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateProblemRequest {
    
    // --- Lesson fields ---
    private String title;
    private String content; 
    private Integer orderIndex;

    // --- Problem fields ---
    private String className = "Solution";
    private String methodName = "solve";
    private String methodSignature = "int, int";
    
    private String starterCode = """
        public class Solution {
            public int solve(int a, int b) {
                // TODO: implement me
                return 0;
            }
        }
        """;

    // --- Test-cases ---
    private List<CreateTestCaseRequest> testCases = new ArrayList<>();
}