package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment; // References enrollment

    private Double quizScore; // Score achieved in quizzes

    private Double assignmentGrade; // Grade for assignments

    private boolean attendanceMarked; // Indicates if attendance has been marked
}
