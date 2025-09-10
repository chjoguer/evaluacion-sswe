package com.example.ocore.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {
    private Long accountId;

    @NotBlank
    private String accountNumber;
    @NotBlank
    private String identification;
    @NotBlank
    @Pattern(regexp = "SAVINGS|CHECKING|BUSINESS", message = "accountType must be one of SAVINGS, CHECKING, BUSINESS")
    private String accountType;

    @NotNull
    private BigDecimal balance;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
