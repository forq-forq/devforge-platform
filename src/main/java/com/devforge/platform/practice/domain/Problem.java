package com.devforge.platform.practice.domain;

import com.devforge.platform.course.domain.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "problem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1 -> 1 relation
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    // The name of the class required for compilation
    @Column(nullable = false)
    private String className;

    // Code template for student
    @Column(columnDefinition = "TEXT", nullable = false)
    private String starterCode;

    // The name of method that we will call in test
    @Column(nullable = false)
    private String methodName;

    // Arguments type signature for parsing
    @Column(nullable = false)
    private String methodSignature;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases;
}