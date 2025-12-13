package com.devforge.platform.practice.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // INPUT
    @Column(columnDefinition = "TEXT")
    private String inputData;

    // OUTPUT
    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;
}