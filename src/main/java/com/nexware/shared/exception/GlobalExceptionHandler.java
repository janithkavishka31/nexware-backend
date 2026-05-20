package com.nexware.shared.exception;

import com.nexware.shared.api.ApiError;
import com.nexware.shared.api.ApiResponse;
import com.nexware.shared.api.ErrorCode;
import com.nexware.shared.api.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldViolation)
                .toList();
        return failure(HttpStatus.BAD_REQUEST, ApiError.validation(
                "Request validation failed",
                request.getRequestURI(),
                traceId(request),
                violations
        ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ResponseEntity<ApiResponse<Void>> handleHandlerMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new FieldViolation(
                                result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage(),
                                null)))
                .toList();
        return failure(HttpStatus.BAD_REQUEST, ApiError.validation(
                "Request validation failed",
                request.getRequestURI(),
                traceId(request),
                violations
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<FieldViolation> violations = exception.getConstraintViolations().stream()
                .map(violation -> new FieldViolation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()))
                .toList();
        return failure(HttpStatus.BAD_REQUEST, ApiError.validation(
                "Request validation failed",
                request.getRequestURI(),
                traceId(request),
                violations
        ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
        return failure(HttpStatus.NOT_FOUND, apiError(exception, request));
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException exception, HttpServletRequest request) {
        return failure(HttpStatus.CONFLICT, apiError(exception, request));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    ResponseEntity<ApiResponse<Void>> handleBusinessRule(
            BusinessRuleViolationException exception,
            HttpServletRequest request
    ) {
        return failure(HttpStatus.UNPROCESSABLE_ENTITY, apiError(exception, request));
    }

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException exception, HttpServletRequest request) {
        return failure(statusFor(exception.errorCode()), apiError(exception, request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation at {}", request.getRequestURI(), exception);
        return failure(HttpStatus.CONFLICT, ApiError.of(
                ErrorCode.CONFLICT,
                "Request violates a data integrity constraint",
                request.getRequestURI(),
                traceId(request)
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException exception, HttpServletRequest request) {
        return failure(HttpStatus.UNAUTHORIZED, ApiError.of(
                ErrorCode.AUTHENTICATION_FAILED,
                "Authentication failed",
                request.getRequestURI(),
                traceId(request)
        ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        return failure(HttpStatus.FORBIDDEN, ApiError.of(
                ErrorCode.ACCESS_DENIED,
                "Access denied",
                request.getRequestURI(),
                traceId(request)
        ));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), exception);
        return failure(HttpStatus.INTERNAL_SERVER_ERROR, ApiError.of(
                ErrorCode.INTERNAL_ERROR,
                "An unexpected error occurred",
                request.getRequestURI(),
                traceId(request)
        ));
    }

    private ResponseEntity<ApiResponse<Void>> failure(HttpStatus status, ApiError error) {
        return ResponseEntity.status(status).body(ApiResponse.failure(error));
    }

    private ApiError apiError(DomainException exception, HttpServletRequest request) {
        return ApiError.of(exception.errorCode(), exception.getMessage(), request.getRequestURI(), traceId(request));
    }

    private HttpStatus statusFor(ErrorCode errorCode) {
        return switch (errorCode) {
            case AUTHENTICATION_FAILED -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case RESOURCE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case VALIDATION_FAILED -> HttpStatus.BAD_REQUEST;
            case BUSINESS_RULE_VIOLATION -> HttpStatus.UNPROCESSABLE_ENTITY;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case EXTERNAL_SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private FieldViolation toFieldViolation(FieldError error) {
        return new FieldViolation(error.getField(), error.getDefaultMessage(), error.getRejectedValue());
    }

    private String traceId(HttpServletRequest request) {
        return request.getHeader("X-Request-Id");
    }
}
