package org.rauka.dm.msaaccountwflux.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("accounts")
public class AccountEntity {

    @Id
    @Column("account_id")
    private Long accountId;

    @Column("account_number")
    private String accountNumber;

    @Column("identification")
    private String identification;

    @Column("account_type")
    private AccountType accountType;

    @Column("balance")
    private BigDecimal balance;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    public enum AccountType {
        AHORRO, CORRIENTE
    }
}
