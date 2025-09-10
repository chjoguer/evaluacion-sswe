package com.example.ocore.domain.exception;

import com.example.ocore.domain.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMovementException extends RuntimeException {
    public InvalidMovementException(String message) {
        super("Invalid movement: " + message);
    }

    @Slf4j
    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(ClientNotFoundException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleClientNotFoundException(ClientNotFoundException ex, ServerWebExchange exchange) {
            log.error("Client not found exception: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Client Not Found")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
        }

        @ExceptionHandler(AccountNotFoundException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleAccountNotFoundException(AccountNotFoundException ex, ServerWebExchange exchange) {
            log.error("Account not found exception: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Account Not Found")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
        }

        @ExceptionHandler(ClientCreationException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleClientCreationException(ClientCreationException ex, ServerWebExchange exchange) {
            log.error("Client creation exception: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Client Creation Failed")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        @ExceptionHandler(AccountCreationException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleAccountCreationException(AccountCreationException ex, ServerWebExchange exchange) {
            log.error("Account creation exception: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Account Creation Failed")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        @ExceptionHandler(InvalidMovementException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleInvalidMovementException(InvalidMovementException ex, ServerWebExchange exchange) {
            log.error("Invalid movement exception: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Invalid Movement")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        @ExceptionHandler(RuntimeException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleRuntimeException(RuntimeException ex, ServerWebExchange exchange) {
            log.error("Runtime exception occurred: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
            log.error("Illegal argument exception occurred: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new com.example.ocore.domain.Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Bad Request")
                    .message(ex.getMessage())
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
        }

        @ExceptionHandler(Exception.class)
        public Mono<ResponseEntity<com.example.ocore.domain.Error>> handleGenericException(Exception ex, ServerWebExchange exchange) {
            log.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

            com.example.ocore.domain.Error error = new Error()
                    .timestamp(OffsetDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message("An unexpected error occurred")
                    .path(exchange.getRequest().getPath().value());

            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
        }
    }
}
