package com.toolschallenge.toolschallenge.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError error = new ApiError(Instant.now(), HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ApiError error = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest request) {
        ApiError error = new ApiError(Instant.now(), HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .collect(Collectors.toList());

        ApiError error = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", "Validation error(s)", request.getRequestURI(), errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        ApiError error = new ApiError(Instant.now(), HttpStatus.CONFLICT.value(), "Data Integrity Violation", ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
