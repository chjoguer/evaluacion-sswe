package org.rauka.dm.msaclient.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.rauka.dm.msaclient.domain.ClientEntity;
import org.rauka.dm.msaclient.service.models.Cliente;
import org.rauka.dm.msaclient.service.models.ClienteCreate;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "fullName", source = "nombre")
    @Mapping(target = "direction", source = "direccion")
    @Mapping(target = "cellphone", source = "telefono")
    @Mapping(target = "password", constant = "***")
    @Mapping(target = "status", source = "estado")
    @Mapping(target = "identification", source = "identificacion")
    @Mapping(target = "documentType", source = "identificacion", qualifiedByName = "determineTipoDocumento")
    Cliente toDTO(ClientEntity client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "fullName")
    @Mapping(target = "direccion", source = "direction")
    @Mapping(target = "telefono", source = "cellphone")
    @Mapping(target = "password", source = "password", conditionExpression = "java(dto.getPassword() != null && !dto.getPassword().isEmpty())")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "clienteId", source = "identification")
    @Mapping(target = "genero", ignore = true)
    @Mapping(target = "edad", ignore = true)
    ClientEntity toEntity(ClienteCreate dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "fullName")
    @Mapping(target = "direccion", source = "direction")
    @Mapping(target = "telefono", source = "cellphone")
    @Mapping(target = "password", source = "password", conditionExpression = "java(!\"***\".equals(dto.getPassword()) && dto.getPassword() != null && !dto.getPassword().isEmpty())")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "genero", ignore = true)
    @Mapping(target = "edad", ignore = true)
    ClientEntity toEntityFromCliente(Cliente dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "fullName")
    @Mapping(target = "direccion", source = "direction")
    @Mapping(target = "telefono", source = "cellphone")
    @Mapping(target = "password", source = "password", conditionExpression = "java(!\"***\".equals(dto.getPassword()) && dto.getPassword() != null && !dto.getPassword().isEmpty())")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "genero", ignore = true)
    @Mapping(target = "edad", ignore = true)
    void updateEntityFromDTO(Cliente dto, @MappingTarget ClientEntity client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombre", source = "fullName")
    @Mapping(target = "direccion", source = "direction")
    @Mapping(target = "telefono", source = "cellphone")
    @Mapping(target = "password", source = "password", conditionExpression = "java(dto.getPassword() != null && !dto.getPassword().isEmpty())")
    @Mapping(target = "estado", source = "status")
    @Mapping(target = "identificacion", source = "identification")
    @Mapping(target = "clienteId", ignore = true)
    @Mapping(target = "genero", ignore = true)
    @Mapping(target = "edad", ignore = true)
    void updateEntityFromCreate(ClienteCreate dto, @MappingTarget ClientEntity client);

    @Named("determineTipoDocumento")
    default String determineTipoDocumento(String documento) {
        if (documento == null) return "CEDULA";

        return switch (documento.length()) {
            case 10 -> "CEDULA";
            case 13 -> "RUC";
            default -> "PASAPORTE";
        };
    }
}