package com.lms.LearningManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequest {
    private Long userId;
    private String subject;
    private String message;
}