package com.lms.learning_management_system;

import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.UserService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTest {
    private static final String TEST_USER = "testUser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";
    private static final String USERNAME_EXISTS = "Username already exists";
    private static final String USER_NOT_FOUND = "User not found";

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USER);
        testUser.setFirstName(TEST_FIRST_NAME);
        testUser.setLastName(TEST_LAST_NAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(TEST_PASSWORD);
        testUser.setRole(User.Role.STUDENT);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        assertNotNull(createdUser);
        assertEquals(TEST_USER, createdUser.getUsername());
        assertEquals(TEST_FIRST_NAME, createdUser.getFirstName());
        assertEquals(TEST_LAST_NAME, createdUser.getLastName());
        assertEquals(TEST_EMAIL, createdUser.getEmail());
        assertEquals(User.Role.STUDENT, createdUser.getRole());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testCreateUserUsernameExists() {
        when(userService.createUser(any(User.class)))
                .thenThrow(new IllegalArgumentException(USERNAME_EXISTS));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(testUser));
        assertEquals(USERNAME_EXISTS, exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        var users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(TEST_USER, users.get(0).getUsername());
        assertEquals(TEST_EMAIL, users.get(0).getEmail());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testGetUserById() {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.of(testUser));

        var user = userService.getUserById(testUser.getId());

        assertTrue(user.isPresent());
        assertEquals(TEST_USER, user.get().getUsername());
        assertEquals(TEST_EMAIL, user.get().getEmail());
        verify(userService, times(1)).getUserById(testUser.getId());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);

        userService.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        doThrow(new IllegalArgumentException(USER_NOT_FOUND))
                .when(userService).deleteUser(1L);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.deleteUser(1L));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
    }
}