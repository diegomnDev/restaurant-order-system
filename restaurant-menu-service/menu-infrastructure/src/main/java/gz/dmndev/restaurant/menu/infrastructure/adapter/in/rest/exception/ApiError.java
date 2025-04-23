package gz.dmndev.restaurant.menu.infrastructure.adapter.in.rest.exception;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;

public record ApiError(
    HttpStatus status, String message, Map<String, String> errors, LocalDateTime timestamp) {}
