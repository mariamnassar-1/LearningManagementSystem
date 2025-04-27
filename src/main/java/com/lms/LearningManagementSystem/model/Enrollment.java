package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // Reference to the student

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Reference to the course

    private LocalDateTime enrollmentDate; // When the enrollment occurred

    private boolean isActive; // Whether the enrollment is active
}