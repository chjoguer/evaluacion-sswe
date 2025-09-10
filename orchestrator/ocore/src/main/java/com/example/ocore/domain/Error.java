package com.example.ocore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error {
    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public Error timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Error status(Integer status) {
        this.status = status;
        return this;
    }

    public Error error(String error) {
        this.error = error;
        return this;
    }

    public Error message(String message) {
        this.message = message;
        return this;
    }

    public Error path(String path) {
        this.path = path;
        return this;
    }
}
