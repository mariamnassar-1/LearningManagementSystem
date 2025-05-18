package com.lms.learning_management_system;

import com.lms.learning_management_system.controller.CustomErrorController;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CustomErrorController customErrorController;

    @BeforeEach
    void setUp() {
        // Initialize the mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleError404() {
        // Mock the request to return a 404 status
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.NOT_FOUND.value());

        // Call the method to handle the error
        String viewName = customErrorController.handleError(request);

        // Verify the result
        assertEquals("error/404", viewName);
    }

    @Test
    void testHandleError403() {
        // Mock the request to return a 403 status
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.FORBIDDEN.value());

        // Call the method to handle the error
        String viewName = customErrorController.handleError(request);

        // Verify the result
        assertEquals("error/403", viewName);
    }

    @Test
    void testHandleErrorOtherError() {
        // Mock the request to return a 500 status
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Call the method to handle the error
        String viewName = customErrorController.handleError(request);

        // Verify the result
        assertEquals("error/error", viewName);
    }

    @Test
    void testHandleErrorNullStatus() {
        // Mock the request to return null (no status)
        when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);

        // Call the method to handle the error
        String viewName = customErrorController.handleError(request);

        // Verify the result
        assertEquals("error/error", viewName);
    }
}
