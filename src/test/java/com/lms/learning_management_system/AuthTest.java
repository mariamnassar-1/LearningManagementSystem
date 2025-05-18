package com.lms.learning_management_system;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.learning_management_system.controller.AuthController;
import com.lms.learning_management_system.model.User;
import com.lms.learning_management_system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private static final String TEST_USER = "testUser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String INVALID_INPUT = "Invalid input";
    private static final String INVALID_CREDENTIALS = "Invalid username or password";
    private static final String LOGOUT_SUCCESS = "Logged out successfully";

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser() throws Exception {
        User user = new User();
        user.setUsername(TEST_USER);
        user.setPassword(TEST_PASSWORD);
        user.setEmail(TEST_EMAIL);
        user.setRole(User.Role.STUDENT);

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USER))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void registerUserWithInvalidInput() throws Exception {
        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException(INVALID_INPUT));

        User user = new User();
        user.setUsername("test");

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(INVALID_INPUT));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void authenticateUserWithInvalidCredentials() throws Exception {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername(TEST_USER);
        loginRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(INVALID_CREDENTIALS));

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(INVALID_CREDENTIALS));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void logoutUser() throws Exception {
        // Create a MockHttpSession instance
        MockHttpSession session = new MockHttpSession();

        // Perform the request and pass the MockHttpSession
        mockMvc.perform(post(LOGOUT_ENDPOINT).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LOGOUT_SUCCESS));

        // Assert that the session is invalidated
        assertTrue(session.isInvalid());
    }
}