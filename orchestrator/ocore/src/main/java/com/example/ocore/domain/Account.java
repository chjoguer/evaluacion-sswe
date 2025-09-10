package com.example.ocore.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private Long clientId;
    private String accountNumber;
    private String identification;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
