package com.devforge.platform.enrollment.domain;

import com.devforge.platform.course.domain.Course;
import com.devforge.platform.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the link between a Student and a Course.
 * Stores progress, grades, and status.
 */
@Entity
@Table(name = "enrollment", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "course_id"}) // Prevent double booking
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;

    private Integer progress; // 0 to 100 percent

    @Column(updatable = false)
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.enrolledAt = LocalDateTime.now();
        this.status = EnrollmentStatus.ACTIVE;
        this.progress = 0;
    }
}