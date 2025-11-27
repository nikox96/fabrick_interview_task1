package com.example.fabrick_interview_task1.exception;

import com.example.fabrick_interview_task1.constant.ApplicationError;
import com.example.fabrick_interview_task1.constant.Status;
import com.example.fabrick_interview_task1.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }


    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalidValue",
                String.class,
                "dateParam",
                null,
                new IllegalArgumentException("Invalid format")
        );

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatch(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.VALIDATION_ERROR.getErrorCode(), body.getErrorCode());
        assertEquals("Invalid value for parameter 'dateParam': invalidValue", body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("fromDate cannot be greater than toDate");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.VALIDATION_ERROR.getErrorCode(), body.getErrorCode());
        assertEquals("fromDate cannot be greater than toDate", body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_ShouldReturnProvidedHttpStatus() {
        NasaApiException exception = new NasaApiException(null, HttpStatus.SERVICE_UNAVAILABLE, ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA.getErrorCode(), body.getErrorCode());
        assertEquals(ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA.getMessage(), body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_WithBadGateway_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException(null, HttpStatus.BAD_GATEWAY, ApplicationError.NASA_RATE_LIMITER_EXCEEDED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.NASA_RATE_LIMITER_EXCEEDED.getErrorCode(), body.getErrorCode());
        assertEquals(ApplicationError.NASA_RATE_LIMITER_EXCEEDED.getMessage(), body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_WithNullHttpStatus_ShouldReturnBadGateway() {
        NasaApiException exception = new NasaApiException(null, null, ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.NASA_UNABLE_TO_RETRIEVE_DATA.getErrorCode(), body.getErrorCode());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.GENERIC_ERROR.getErrorCode(), body.getErrorCode());
        assertEquals(ApplicationError.GENERIC_ERROR.getMessage(), body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleIllegalArgumentException_WithComplexMessage_ShouldReturnBadRequest() {
        String complexMessage = "Multiple validation errors: fromDate is invalid, toDate is invalid";
        IllegalArgumentException exception = new IllegalArgumentException(complexMessage);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertEquals(complexMessage, body.getMessage());
        assertEquals(ApplicationError.VALIDATION_ERROR.getErrorCode(), body.getErrorCode());
    }

    @Test
    void handleNasaApiException_WithNotFound_ShouldReturnNotFound() {
        NasaApiException exception = new NasaApiException(null, HttpStatus.NOT_FOUND, ApplicationError.NASA_ASTEROID_NOT_FOUND);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.NASA_ASTEROID_NOT_FOUND.getErrorCode(), body.getErrorCode());
        assertEquals(ApplicationError.NASA_ASTEROID_NOT_FOUND.getMessage(), body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void handleNasaApiException_WithTooManyRequests_ShouldReturnTooManyRequests() {
        NasaApiException exception = new NasaApiException(null, HttpStatus.TOO_MANY_REQUESTS, ApplicationError.NASA_RATE_LIMITER_EXCEEDED);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNasaApiException(exception, webRequest);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());

        ErrorResponse body = response.getBody();
        assertEquals(Status.ERROR, body.getStatus());
        assertEquals(ApplicationError.NASA_RATE_LIMITER_EXCEEDED.getErrorCode(), body.getErrorCode());
        assertEquals(ApplicationError.NASA_RATE_LIMITER_EXCEEDED.getMessage(), body.getMessage());
        assertEquals("/api/test", body.getPath());
        assertNotNull(body.getTimestamp());
    }
}