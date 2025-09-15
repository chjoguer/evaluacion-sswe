package org.rauka.dm.msapdfgenerator.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("movements")
public class MovementEntity {

    @Id
    @Column("movement_id")
    private Long movementId;

    @Column("unique_key")
    private String uniqueKey;

    @Column("account_id")
    private Long accountId;

    @Column("occurred_at")
    private LocalDateTime occurredAt;

    @Column("movement_type")
    private MovementType movementType;

    @Column("amount")
    private BigDecimal amount;

    @Column("balance")
    private BigDecimal balance;

    @Column("description")
    private String description;

    @Column("reference")
    private String reference;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public enum MovementType {
        DEBIT, CREDIT
    }
}
