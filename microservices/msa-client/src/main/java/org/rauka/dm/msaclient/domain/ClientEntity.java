package org.rauka.dm.msaclient.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "clientes")
@Data
@EqualsAndHashCode(callSuper = true)
public class ClientEntity extends PersonEntity {

    @Column(unique = true, nullable = false)
    private String clienteId;

    @NotBlank
    @Size(min = 8, max = 128)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,128}$",
            message = "Contraseña debe tener mayúscula, minúscula, número y caracter especial"
    )
    private String password;

    @NotNull
    private Boolean estado;
}
