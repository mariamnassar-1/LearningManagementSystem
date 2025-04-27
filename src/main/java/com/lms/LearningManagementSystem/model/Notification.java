package com.lms.LearningManagementSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String subject;
    private String message;
    private LocalDateTime timestamp;
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "user_id") // Updated to associate with the User entity
    private User user;
}
