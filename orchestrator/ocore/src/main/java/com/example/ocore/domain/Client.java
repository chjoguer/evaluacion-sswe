package com.example.ocore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    private String id;
    private String fullName;
    private String direction;
    private String cellphone;
    private String password;
    private Boolean status;
    private String documentType;
    private String identification;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
