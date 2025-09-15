package org.rauka.dm.msapdfgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Boolean status;
    private String identification; // Este campo nos permitirá obtener el cliente
    private String clientId;
}
