package org.rauka.dm.msaaccountwflux.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException occurred: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", ex.getStatusCode().value(),
                "error", ex.getStatusCode(),
                "message", ex.getReason() != null ? ex.getReason() : "An error occurred",
                "path", ex.getMessage()
        );
        
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException occurred: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", ex.getMessage(),
                "path", "/api/accounts"
        );
        
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> errorResponse = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred",
                "path", "/api/accounts"
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}
