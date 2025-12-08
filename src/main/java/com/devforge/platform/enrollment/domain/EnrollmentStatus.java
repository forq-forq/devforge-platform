package com.devforge.platform.enrollment.domain;

/**
 * Represents the state of a student's enrollment in a course.
 */
public enum EnrollmentStatus {
    ACTIVE,     // Student is currently learning
    COMPLETED,  // Student finished all lessons
    DROPPED     // Student quit the course
}