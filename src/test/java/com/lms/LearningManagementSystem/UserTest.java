package com.lms.LearningManagementSystem;

import com.lms.LearningManagementSystem.model.User;
import com.lms.LearningManagementSystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks


    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setRole(User.Role.STUDENT);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals("testUser", createdUser.getUsername());
        assertEquals("Test", createdUser.getFirstName());
        assertEquals("User", createdUser.getLastName());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals(User.Role.STUDENT, createdUser.getRole());
        verify(userService, times(1)).createUser(any(User.class));
    }


    @Test
    void testCreateUser_UsernameExists() {
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(testUser));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        var users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
        assertEquals("test@example.com", users.get(0).getEmail());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.of(testUser));

        var user = userService.getUserById(testUser.getId());

        assertTrue(user.isPresent());
        assertEquals("testUser", user.get().getUsername());
        assertEquals("test@example.com", user.get().getEmail());
        verify(userService, times(1)).getUserById(testUser.getId());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        userService.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        doThrow(new IllegalArgumentException("User not found"))
                .when(userService).deleteUser(1L);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(1L));
        assertEquals("User not found", exception.getMessage());
    }
}