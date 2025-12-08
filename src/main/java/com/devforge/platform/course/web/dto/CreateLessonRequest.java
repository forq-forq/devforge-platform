package com.devforge.platform.course.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for adding a new lesson to a course.
 */
public record CreateLessonRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Content is required")
    String content,

    String videoUrl,

    @NotNull(message = "Order index is required")
    @Positive
    Integer orderIndex
) {}