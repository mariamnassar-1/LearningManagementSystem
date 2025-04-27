package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String text;

    @NotBlank
    private String options; // This should be converted into a proper list in the future

    @NotBlank
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false) // Foreign key column
    @JsonIgnoreProperties("questions") // Prevents circular reference during serialization
    private Course course;
}
