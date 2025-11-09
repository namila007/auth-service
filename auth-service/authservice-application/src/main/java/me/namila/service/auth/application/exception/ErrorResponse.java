package me.namila.service.auth.application.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

/**
 * Error response DTO for API error responses.
 */
@Getter
@Setter
@Builder
public class ErrorResponse {
    
    private String errorCode;
    private String message;
    private Map<String, String> errors;
    private Instant timestamp;
}

