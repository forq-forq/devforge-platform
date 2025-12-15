package com.devforge.platform.common.service;

import com.devforge.platform.common.service.dto.GeminiRequest;
import com.devforge.platform.common.service.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String apiUrl;

    private final RestClient restClient = RestClient.create();

    // Lecture Summary
    public String summarizeLecture(String content) {
        String prompt = String.format("""
                You are a tech tutor. Summarize this lesson into 3-5 key bullet points using emojis.
                Keep it concise and beginner-friendly.
                
                Lesson Content:
                %s
                """, content);
        return callApi(prompt);
    }

    // Errors explanation
    public String explainError(String task, String code, String error) {
        String prompt = String.format("""
                You are a helper for a Java student.
                Task: %s
                Code:
                %s
                Error: %s
                
                Explain briefly (2 sentences) what is wrong. Give a hint, DO NOT write the solution.
                """, task, code, error);
        return callApi(prompt);
    }

    // Code Review
    public String reviewCode(String task, String code) {
        String prompt = String.format("""
                Student solved this task: %s
                Code:
                %s
                
                Give a brief Code Review (Clean Code, Naming).
                If it's good, praise them. If can be improved, suggest 1 change.
                """, task, code);
        return callApi(prompt);
    }

    // Correct method for request
    private String callApi(String prompt) {
        try {
            var request = new GeminiRequest(List.of(
                new GeminiRequest.Content(List.of(new GeminiRequest.Part(prompt)))
            ));

            var response = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
            return "AI is silent.";
        } catch (Exception e) {
            log.error("AI Error", e);
            return "AI Service Unavailable.";
        }
    }
}