package com.example.ocore.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private String id;
    private String fullName;
    private String direction;
    private String cellphone;
    private String password;
    private Boolean status;
    private String documentType;
    private String identification;
}
