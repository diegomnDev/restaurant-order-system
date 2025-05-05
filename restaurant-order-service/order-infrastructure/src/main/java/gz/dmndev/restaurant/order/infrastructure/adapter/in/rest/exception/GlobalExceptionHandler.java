package gz.dmndev.restaurant.order.infrastructure.adapter.in.rest.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<ApiError> handleIllegalStateException(
      IllegalStateException ex, WebRequest request) {
    return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, WebRequest request) {
    List<ApiError.ValidationError> validationErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    ApiError.ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
            .collect(Collectors.toList());

    ApiError apiError =
        ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation error")
            .path(getPath(request))
            .timestamp(LocalDateTime.now())
            .validationErrors(validationErrors)
            .build();

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiError> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    List<ApiError.ValidationError> validationErrors =
        ex.getConstraintViolations().stream()
            .map(
                violation ->
                    ApiError.ValidationError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .build())
            .collect(Collectors.toList());

    ApiError apiError =
        ApiError.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation error")
            .path(getPath(request))
            .timestamp(LocalDateTime.now())
            .validationErrors(validationErrors)
            .build();

    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
    return createErrorResponse(
        "An unexpected error occurred: " + ex.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

  private ResponseEntity<ApiError> createErrorResponse(
      String message, HttpStatus status, WebRequest request) {
    ApiError apiError =
        ApiError.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(getPath(request))
            .timestamp(LocalDateTime.now())
            .build();

    return new ResponseEntity<>(apiError, status);
  }

  private String getPath(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }
}
