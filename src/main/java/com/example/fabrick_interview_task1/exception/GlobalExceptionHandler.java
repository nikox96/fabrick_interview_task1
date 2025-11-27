package com.example.fabrick_interview_task1.exception;

import com.example.fabrick_interview_task1.constant.ApplicationError;
import com.example.fabrick_interview_task1.constant.Status;
import com.example.fabrick_interview_task1.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.error("Type mismatch error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(Status.ERROR)
                .errorCode(ApplicationError.VALIDATION_ERROR.getErrorCode())
                .message(String.format("Invalid value for parameter '%s': %s", ex.getName(), ex.getValue()))
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Illegal argument error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(Status.ERROR)
                .errorCode(ApplicationError.VALIDATION_ERROR.getErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NasaApiException.class)
    public ResponseEntity<ErrorResponse> handleNasaApiException(
            NasaApiException ex, WebRequest request) {

        log.error("NASA API error: {} (status: {})", ex.getMessage(), ex.getHttpStatus(), ex);

        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_GATEWAY;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(Status.ERROR)
                .errorCode(ex.getError().getErrorCode())
                .message(ex.getError().getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleNasaApiException(
            ApplicationException ex, WebRequest request) {

        log.error("Application exception error: {} (status: {})", ex.getMessage(), ex.getHttpStatus(), ex);

        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(Status.ERROR)
                .errorCode(ex.getError().getErrorCode())
                .message(ex.getError().getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(Status.ERROR)
                .errorCode(ApplicationError.GENERIC_ERROR.getErrorCode())
                .message(ApplicationError.GENERIC_ERROR.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}