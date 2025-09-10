package com.example.ocore.infrastructure.dto;

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
public class MovementCreateDTO {
    private String uniqueKey;
    private Long accountId;
    private OffsetDateTime occurredAt;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private String reference;
}
