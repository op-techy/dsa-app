package com.nomba.dsaapp.exception;

import com.nomba.dsaapp.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DsaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDsaNotFound(DsaNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(GeofenceViolationException.class)
    public ResponseEntity<ErrorResponse> handleGeofenceViolation(GeofenceViolationException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(SessionAlreadyCheckedOutException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCheckedOut(SessionAlreadyCheckedOutException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    @ExceptionHandler(UnauthorizedSessionAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedSession(UnauthorizedSessionAccessException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}