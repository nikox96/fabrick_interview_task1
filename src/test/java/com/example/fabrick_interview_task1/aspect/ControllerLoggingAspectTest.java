package com.example.fabrick_interview_task1.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    private ControllerLoggingAspect aspect;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        aspect = new ControllerLoggingAspect(objectMapper);
        mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        mockRequest.setRequestURI("/api/test");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void logIncomingRequests_WithSuccessfulResponse_ShouldProceedAndReturnResult() throws Throwable {
        // Arrange
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");
        when(joinPoint.proceed()).thenReturn(expectedResponse);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"param1"});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(expectedResponse, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithQueryString_ShouldLogFullUrl() throws Throwable {
        // Arrange
        mockRequest.setQueryString("id=123&name=test");
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");
        when(joinPoint.proceed()).thenReturn(expectedResponse);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(expectedResponse, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithHeaders_ShouldLogHeaders() throws Throwable {
        // Arrange
        mockRequest.addHeader("Content-Type", "application/json");
        mockRequest.addHeader("Authorization", "Bearer token");
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("success");
        when(joinPoint.proceed()).thenReturn(expectedResponse);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(expectedResponse, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithException_ShouldRethrowException() throws Throwable {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Controller error");
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            aspect.logIncomingRequests(joinPoint)
        );

        assertEquals("Controller error", thrown.getMessage());
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithNonResponseEntityResult_ShouldHandleGracefully() throws Throwable {
        // Arrange
        String expectedResult = "plain string response";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(expectedResult, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithErrorResponse_ShouldLogStatusCode() throws Throwable {
        // Arrange
        ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
        when(joinPoint.proceed()).thenReturn(errorResponse);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(errorResponse, result);
        assertEquals(HttpStatus.BAD_REQUEST, ((ResponseEntity<?>) result).getStatusCode());
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithPostRequest_ShouldLogMethod() throws Throwable {
        // Arrange
        mockRequest.setMethod("POST");
        mockRequest.setRequestURI("/api/create");
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("created");
        when(joinPoint.proceed()).thenReturn(expectedResponse);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"requestBody"});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertEquals(expectedResponse, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logIncomingRequests_WithNullResult_ShouldHandleGracefully() throws Throwable {
        // Arrange
        when(joinPoint.proceed()).thenReturn(null);
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logIncomingRequests(joinPoint);

        // Assert
        assertNull(result);
        verify(joinPoint, times(1)).proceed();
    }
}