package org.rauka.dm.msapdfgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementReportDTO {

    private LocalDateTime occurredAt;
    private String description;
    private Long accountId;
    private String movementType;
    private String status;
    private BigDecimal initialAmount; // (m.balance - m.amount) AS A
    private BigDecimal amount;
    private BigDecimal balance;
    private String reference;
    private LocalDateTime createdAt;
}
