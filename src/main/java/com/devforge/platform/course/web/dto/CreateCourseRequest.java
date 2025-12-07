package com.devforge.platform.course.web.dto;

import com.devforge.platform.course.domain.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new course.
 * Status is not included because new courses are always DRAFT by default.
 */
public record CreateCourseRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    String title,

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    String description,

    @NotNull(message = "Course level is required")
    CourseLevel level
) {}