package com.tracebill.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tracebill.dto.ApiResponse;
import com.tracebill.dto.ErrorResponse;
import com.tracebill.module.auth.exception.UserIdAndPasswordNotMatchException;
//import com.tracebill.module.production.exception.FactoryNotFoundException;
//import com.tracebill.module.production.exception.ProductNotFoundException;
import com.tracebill.module.user.exception.UserAlreadyCreatedException;
import com.tracebill.module.user.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	// ------------------- 404 NOT FOUND -------------------
    @ExceptionHandler({ UsernameNotFoundException.class, UserNotFoundException.class  , ResourceNotFoundException.class /*, ProductNotFoundException.class , FactoryNotFoundException.class*/})
    public ResponseEntity<ApiResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    
    
    // ------------------- 409 CONFLICT -------------------
    @ExceptionHandler({UserAlreadyCreatedException.class , IllegalStateException.class})
    public ResponseEntity<ApiResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
        log.warn("Conflict: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // ------------------- 401 UNAUTHORIZED -------------------
    @ExceptionHandler({ UnauthorizedUserException.class, UserIdAndPasswordNotMatchException.class })
    public ResponseEntity<ApiResponse> handleUnauthorized(RuntimeException ex, HttpServletRequest request) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    // ------------------- 400 BAD REQUEST (Validation) -------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // Take the first validation error as main message for clarity
        String mainMessage = ex.getBindingResult().getFieldErrors().isEmpty()
                ? "Validation failed"
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ErrorResponse err = new ErrorResponse(LocalDateTime.now(), mainMessage, request.getRequestURI());
        err.setValidationErrors(errors);

        log.warn("Validation error at {}: {}", request.getRequestURI(), errors);
        return new ResponseEntity<>(new ApiResponse(false, err, null), HttpStatus.BAD_REQUEST);
    }
    
 // ------------------- 401 UNAUTHORIZED (JWT / AUTH) -------------------
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
    }

    // ------------------- 403 FORBIDDEN (JWT / AUTH) -------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request.getRequestURI()
        );
    }

    

    // ------------------- 500 INTERNAL SERVER ERROR -------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // ------------------- Utility -------------------
    private ResponseEntity<ApiResponse> buildErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponse err = new ErrorResponse(LocalDateTime.now(), message, path);
        
        return new ResponseEntity<>(new ApiResponse(false, err, message), status);
    }
}
