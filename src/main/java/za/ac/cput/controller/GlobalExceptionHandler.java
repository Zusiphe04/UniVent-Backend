package za.ac.cput.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.ConstraintViolationException;
import za.ac.cput.dtos.ApiError;
import za.ac.cput.exception.AlreadyBookedException;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.EventFullException;
import za.ac.cput.exception.EventNotAvailableException;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.exception.InvalidCredentialsException;
import za.ac.cput.exception.NotificationNotFoundException;
import za.ac.cput.exception.StaffNumberAlreadyExistsException;
import za.ac.cput.exception.UserNotFoundException;
import za.ac.cput.util.UnauthorizedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiError> handleEventNotFound(EventNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "EVENT_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(EventStateConflictException.class)
    public ResponseEntity<ApiError> handleEventStateConflict(EventStateConflictException exception,
                                                             HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage(), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception,
                                                         HttpServletRequest request) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        return error(status, status.getReasonPhrase(), exception.getReason(), request);
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ApiError> handleEventFull(EventFullException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "EVENT_FULL", exception.getMessage(), request);
    }

    @ExceptionHandler(EventNotAvailableException.class)
    public ResponseEntity<ApiError> handleUnavailableEvent(RuntimeException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "EVENT_NOT_AVAILABLE", exception.getMessage(), request);
    }

    @ExceptionHandler(AlreadyBookedException.class)
    public ResponseEntity<ApiError> handleAlreadyBooked(AlreadyBookedException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "ALREADY_BOOKED", exception.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "UNAUTHORIZED", exception.getMessage(), request);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicateEmail(EmailAlreadyExistsException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", exception.getMessage(), request);
    }

    @ExceptionHandler(StaffNumberAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleDuplicateStaffNumber(StaffNumberAlreadyExistsException exception,
                                                                HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "STAFF_NUMBER_ALREADY_EXISTS", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", exception.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ApiError> handleNotificationNotFound(NotificationNotFoundException exception,
                                                                HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception,
                                                     HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Request validation failed");
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleMalformedRequest(Exception exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request parameters or body are invalid", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataConflict(DataIntegrityViolationException exception,
                                                       HttpServletRequest request) {
        String causeMessage = exception.getMostSpecificCause().getMessage();
        if (causeMessage != null && causeMessage.contains("ux_users_staff_number")) {
            return error(HttpStatus.CONFLICT, "STAFF_NUMBER_ALREADY_EXISTS",
                    "An account with this staff number already exists", request);
        }
        if (causeMessage != null && causeMessage.contains("ux_users_email")) {
            return error(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS",
                    "An account with this email already exists", request);
        }
        return error(HttpStatus.CONFLICT, "CONFLICT", "The request conflicts with existing data", request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception,
                                                          HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), request);
    }

    private ResponseEntity<ApiError> error(HttpStatus status,
                                           String code,
                                           String message,
                                           HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), code, message, request.getRequestURI()));
    }
}
