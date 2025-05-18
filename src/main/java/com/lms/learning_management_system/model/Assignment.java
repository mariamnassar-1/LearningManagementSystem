package com.lms.learning_management_system.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


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

    @JsonProperty("studentName")
    public String getStudentName() {
        return student != null ? student.getUsername() : null;
    }

    public void setStudentName(String muhammadFathi) {
        if (this.student == null) {
            this.student = new User();  // initialize student if null
        }
        this.student.setUsername(muhammadFathi);
    }
}
