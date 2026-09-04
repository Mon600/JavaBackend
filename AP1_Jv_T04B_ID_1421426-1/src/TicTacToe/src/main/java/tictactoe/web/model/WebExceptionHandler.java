package tictactoe.web.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;


@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NoSuchElementException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Not Found",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Invalid data",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleInvalidUUID(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(UUID.class)) {
            return Map.of(
                    "timestamp", LocalDateTime.now(),
                    "status", 404,
                    "error", "Not found",
                    "message", "Game not found"
            );
        }
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Invalid data",
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 409,
                "error", "Invalid data",
                "message", ex.getMessage()
        );
    }
}
