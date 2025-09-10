package org.rauka.dm.msamovementwflux.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.rauka.dm.msamovementwflux.domain.MovementEntity;
import org.rauka.dm.msamovementwflux.service.models.MovementCreateDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MovementMapper {

    @Mapping(target = "movementId", source = "movementId")
    @Mapping(target = "movementType", source = "movementType", qualifiedByName = "movementTypeToEnum")
    @Mapping(target = "occurredAt", source = "occurredAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    MovementDTO toDto(MovementEntity movementEntity);

    @Mapping(target = "movementId", ignore = true)
    @Mapping(target = "movementType", source = "movementType", qualifiedByName = "enumToMovementType")
    @Mapping(target = "occurredAt", source = "occurredAt", qualifiedByName = "offsetDateTimeToLocalDateTime")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MovementEntity toEntity(MovementDTO movementDTO);

    @Mapping(target = "movementId", ignore = true)
    @Mapping(target = "uniqueKey", source = ".", qualifiedByName = "generateRandomUuid")    @Mapping(target = "movementType", source = "movementType", qualifiedByName = "enumToMovementType")
    @Mapping(target = "occurredAt", source = "occurredAt", qualifiedByName = "offsetDateTimeToLocalDateTime")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MovementEntity createDtoToEntity(MovementCreateDTO movementCreateDTO);

    @Named("movementTypeToEnum")
    default MovementType movementTypeToEnum(MovementEntity.MovementType movementType) {
        if (movementType == null) return null;
        return MovementType.valueOf(movementType.name());
    }

    @Named("enumToMovementType")
    default MovementEntity.MovementType enumToMovementType(MovementType movementType) {
        if (movementType == null) return null;
        return MovementEntity.MovementType.valueOf(movementType.name());
    }

    @Named("localDateTimeToOffsetDateTime")
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atOffset(ZoneOffset.UTC);
    }

    @Named("offsetDateTimeToLocalDateTime")
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) return null;
        return offsetDateTime.toLocalDateTime();
    }

    @Named("generateRandomUuid")
    default String generateRandomUuid(MovementCreateDTO dto) {
        return UUID.randomUUID().toString();
    }
}
