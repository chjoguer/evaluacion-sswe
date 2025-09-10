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
public class Movement {
    private Long id;
    private String uniqueKey;
    private Long accountId;
    private OffsetDateTime occurredAt;
    private String movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private String reference;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
