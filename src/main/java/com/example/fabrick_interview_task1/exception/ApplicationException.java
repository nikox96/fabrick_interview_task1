package com.example.fabrick_interview_task1.exception;

import com.example.fabrick_interview_task1.constant.ApplicationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class ApplicationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ApplicationError error;

}