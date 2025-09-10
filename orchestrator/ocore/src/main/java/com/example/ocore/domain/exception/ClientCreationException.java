package com.example.ocore.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientCreationException extends RuntimeException {
    public ClientCreationException(String message) {
        super("Failed to create client: " + message);
    }

    public ClientCreationException(String message, Throwable cause) {
        super("Failed to create client: " + message, cause);
    }
}
