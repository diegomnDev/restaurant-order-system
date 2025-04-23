package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, "Validation error", errors, LocalDateTime.now());

    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.status());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), null, LocalDateTime.now());

    return new ResponseEntity<>(apiError, apiError.status());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException ex) {
    ApiError apiError =
        new ApiError(HttpStatus.CONFLICT, ex.getMessage(), null, LocalDateTime.now());

    return new ResponseEntity<>(apiError, apiError.status());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex) {
    ApiError apiError =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred",
            Map.of("error", ex.getMessage()),
            LocalDateTime.now());

    return new ResponseEntity<>(apiError, apiError.status());
  }
}
