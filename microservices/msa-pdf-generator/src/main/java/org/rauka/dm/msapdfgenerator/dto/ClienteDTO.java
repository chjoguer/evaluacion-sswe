package org.rauka.dm.msapdfgenerator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private String identificacion;
    private String fullName;
    private String documentType;
    private Integer edad;
    private String direction;
    private String cellphone;
    private String clienteId;
    private Boolean estado;
}
