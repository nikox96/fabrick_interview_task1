package com.example.fabrick_interview_task1.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiLoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private ExternalApiLoggingAspect aspect;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        aspect = new ExternalApiLoggingAspect(objectMapper);
    }

    @Test
    void logExternalApiCalls_WithSuccessfulCall_ShouldProceedAndReturnResult() throws Throwable {
        // Arrange
        String expectedResult = "success";
        when(joinPoint.proceed()).thenReturn(expectedResult);
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 123});

        // Act
        Object result = aspect.logExternalApiCalls(joinPoint);

        // Assert
        assertEquals(expectedResult, result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logExternalApiCalls_WithException_ShouldRethrowException() throws Throwable {
        // Arrange
        RuntimeException expectedException = new RuntimeException("API error");
        when(joinPoint.proceed()).thenThrow(expectedException);
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
            aspect.logExternalApiCalls(joinPoint)
        );

        assertEquals("API error", thrown.getMessage());
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logExternalApiCalls_WithNullArgs_ShouldHandleGracefully() throws Throwable {
        // Arrange
        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(null);

        // Act
        Object result = aspect.logExternalApiCalls(joinPoint);

        // Assert
        assertEquals("result", result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logExternalApiCalls_WithEmptyArgs_ShouldHandleGracefully() throws Throwable {
        // Arrange
        when(joinPoint.proceed()).thenReturn("result");
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        Object result = aspect.logExternalApiCalls(joinPoint);

        // Assert
        assertEquals("result", result);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void logExternalApiCalls_WithNullResult_ShouldHandleGracefully() throws Throwable {
        // Arrange
        when(joinPoint.proceed()).thenReturn(null);
        when(joinPoint.getTarget()).thenReturn(this);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"param"});

        // Act
        Object result = aspect.logExternalApiCalls(joinPoint);

        // Assert
        assertNull(result);
        verify(joinPoint, times(1)).proceed();
    }
}