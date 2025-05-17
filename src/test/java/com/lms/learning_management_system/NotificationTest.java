package com.lms.learning_management_system;

import com.lms.learning_management_system.controller.NotificationController;
import com.lms.learning_management_system.model.Notification;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.NotificationService;
import com.lms.learning_management_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the standalone setup
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
    }

    @Test
    void createNotification() throws Exception {
        // Mock userService behavior
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        when(userService.findById(1L)).thenReturn(mockUser);

        // Mock notificationService behavior
        doNothing().when(notificationService).createNotification(any(User.class), anyString(), anyString());

        // Test request payload
        String requestBody = "{\"userId\": 1, \"subject\": \"Test Subject\", \"message\": \"Test Message\"}";

        mockMvc.perform(post("/notifications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification created successfully."));

        // Verify interactions
        verify(userService, times(1)).findById(1L);
        verify(notificationService, times(1)).createNotification(mockUser, "Test Subject", "Test Message");
    }

    @Test
    void getUnreadNotifications() throws Exception {
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setSubject("Subject 1");
        notification1.setMessage("Message 1");
        notification1.setTimestamp(LocalDateTime.now());
        notification1.setIsRead(false);

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setSubject("Subject 2");
        notification2.setMessage("Message 2");
        notification2.setTimestamp(LocalDateTime.now());
        notification2.setIsRead(false);

        List<Notification> notifications = Arrays.asList(notification1, notification2);

        when(notificationService.getUnreadNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/unread/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(notificationService, times(1)).getUnreadNotifications(1L);
    }

    @Test
    void getAllNotifications() throws Exception {
        Notification notification = new Notification();
        notification.setId(1L);
        notification.setSubject("All Subject");
        notification.setMessage("All Message");
        notification.setTimestamp(LocalDateTime.now());

        List<Notification> notifications = List.of(notification);

        when(notificationService.getAllNotifications(1L)).thenReturn(notifications);

        mockMvc.perform(get("/notifications/all/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].subject").value("All Subject"));

        verify(notificationService, times(1)).getAllNotifications(1L);
    }

//    error not found 404
    @Test
    void markAsRead() throws Exception {
        long notificationId = 1;

        // Mock the behavior of the markAsRead method
        when(notificationService.markAsRead(notificationId)).thenReturn(true);

        mockMvc.perform(post("/notifications/read/{id}", notificationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

}