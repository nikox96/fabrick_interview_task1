package com.example.fabrick_interview_task1.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApplicationError {
    //NASA Errors
    NASA_ASTEROID_NOT_FOUND("Asteroid not found", 4001)
    ,NASA_RATE_LIMITER_EXCEEDED("Service rate limit exceeded", 4002)
    ,NASA_UNABLE_TO_RETRIEVE_DATA("Unable to retrieve asteroid data", 4003)
    //Generic Errors
    ,VALIDATION_ERROR("Validation error", 9998)
    ,GENERIC_ERROR("Generic error", 9999);

    private final String message;
    private final Integer errorCode;
}
