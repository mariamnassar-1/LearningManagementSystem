package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String otp; // OTP for attendance

    @ManyToOne
    @JoinColumn(name = "course_id") // Reference to the course this lesson belongs to
    @JsonIgnoreProperties({"lessons", "enrolledStudents", "instructor"})
    private Course course;
}