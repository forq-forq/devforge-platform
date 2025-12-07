package com.devforge.platform.course.domain;

/**
 * Represents the lifecycle status of a course.
 * - DRAFT: Visible only to the instructor.
 * - PUBLISHED: Visible to all students.
 * - ARCHIVED: Hidden from the catalog but accessible to enrolled students.
 */
public enum CourseStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}