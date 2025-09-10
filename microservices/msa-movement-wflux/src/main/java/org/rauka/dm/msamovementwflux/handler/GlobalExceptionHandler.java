package org.rauka.dm.msamovementwflux.handler;

import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msamovementwflux.service.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<Error>> handleRuntimeException(RuntimeException ex, ServerWebExchange exchange) {
        log.error("Runtime exception occurred: {}", ex.getMessage(), ex);
        
        Error error = new Error()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Error>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        log.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);
        
        Error error = new Error()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Error>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);
        
        Error error = new Error()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(exchange.getRequest().getPath().value());
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
