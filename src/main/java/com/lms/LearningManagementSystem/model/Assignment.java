package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // foreign key column
    private User student;

    @NotBlank
    private String content;

    private Double grade;

    @NotBlank
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)  // foreign key column
    private Course course;

    public void setStudentName(String muhammadFathi) {
        student.setUsername(muhammadFathi);
    }
}
