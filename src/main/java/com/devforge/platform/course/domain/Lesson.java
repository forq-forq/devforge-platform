package com.devforge.platform.course.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a single learning unit within a Course.
 * Can contain text content, video links, or coding exercises.
 */
@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    /**
     * Main content of the lesson (Markdown or HTML support anticipated).
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Optional URL to a video lecture (e.g., YouTube embed).
     */
    private String videoUrl;

    /**
     * Determines the order of lessons in the course curriculum.
     */
    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * The course this lesson belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default // Lombok will use this by default
    private LessonType type = LessonType.LECTURE;
}