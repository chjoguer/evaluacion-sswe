package org.rauka.dm.msaclient.handler;

import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msaclient.service.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());

        Error error = createError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleRuntimeException(RuntimeException ex) {
        log.error("Internal server error", ex);

        Error error = createError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        Error error = createError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private Error createError(Integer code, String message) {
        Error error = new Error();
        error.setMessage(message);
        error.setStatus(code);
        error.setError(HttpStatus.valueOf(code).getReasonPhrase());
        error.setPath(getCurrentPath());
        error.setTimestamp(java.time.OffsetDateTime.now());

        return error;
    }

    private String getCurrentPath() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest().getRequestURI();
        }
        return "N/A";
    }
}
