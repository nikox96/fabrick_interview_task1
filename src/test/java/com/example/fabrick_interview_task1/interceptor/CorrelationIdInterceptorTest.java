package com.example.fabrick_interview_task1.interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdInterceptorTest {

    private CorrelationIdInterceptor interceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new CorrelationIdInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void preHandle_WithoutCorrelationIdHeader_ShouldGenerateNewUuid() {
        // Act
        boolean result = interceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        String correlationId = MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY);
        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
        assertEquals(correlationId, response.getHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER));
    }

    @Test
    void preHandle_WithCorrelationIdHeader_ShouldUseProvidedId() {
        // Arrange
        String providedCorrelationId = "test-correlation-id-123";
        request.addHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER, providedCorrelationId);

        // Act
        boolean result = interceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        assertEquals(providedCorrelationId, MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY));
        assertEquals(providedCorrelationId, response.getHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER));
    }

    @Test
    void preHandle_WithBlankCorrelationIdHeader_ShouldGenerateNewUuid() {
        // Arrange
        request.addHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER, "   ");

        // Act
        boolean result = interceptor.preHandle(request, response, new Object());

        // Assert
        assertTrue(result);
        String correlationId = MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY);
        assertNotNull(correlationId);
        assertNotEquals("   ", correlationId);
    }

    @Test
    void afterCompletion_ShouldRemoveCorrelationIdFromMdc() {
        // Arrange
        MDC.put(CorrelationIdInterceptor.CORRELATION_ID_KEY, "test-id");

        // Act
        interceptor.afterCompletion(request, response, new Object(), null);

        // Assert
        assertNull(MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY));
    }

    @Test
    void afterCompletion_WithException_ShouldStillRemoveCorrelationId() {
        // Arrange
        MDC.put(CorrelationIdInterceptor.CORRELATION_ID_KEY, "test-id");
        Exception exception = new RuntimeException("Test exception");

        // Act
        interceptor.afterCompletion(request, response, new Object(), exception);

        // Assert
        assertNull(MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY));
    }

    @Test
    void generatedCorrelationId_ShouldBeValidUuid() {
        // Act
        interceptor.preHandle(request, response, new Object());

        // Assert
        String correlationId = MDC.get(CorrelationIdInterceptor.CORRELATION_ID_KEY);
        assertDoesNotThrow(() -> java.util.UUID.fromString(correlationId));
    }
}