package org.rauka.dm.msaclient.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank
    @Pattern(regexp = "^(M|F|O)$", message = "Genero debe ser M, F u O")
    private String genero;

    @Min(0)
    @Max(120)
    private Integer edad;

    @NotBlank
    @Column(unique = true, nullable = false, length = 20)
    private String identificacion;

    @NotBlank
    @Size(min = 5, max = 200)
    private String direccion;

    @NotBlank
    @Pattern(regexp = "^(\\+593|0)9\\d{8}$", message = "Teléfono inválido (Ecuador)")
    private String telefono;
}
