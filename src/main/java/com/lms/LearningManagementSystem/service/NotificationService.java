package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Notification;
import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;

    public NotificationService(NotificationRepository notificationRepository, EmailNotificationService emailNotificationService) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public void createNotification(User user, String subject, String message) {
        // Save notification in database
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setSubject(subject);
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsRead(false);
        notificationRepository.save(notification);

        // Send email notification
        emailNotificationService.sendEmail(user.getEmail(), subject, message);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public boolean markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return true;  // Successfully marked as read
        }
        return false;  // Notification not found
    }
}
