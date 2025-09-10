package org.rauka.dm.msaaccountwflux.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.rauka.dm.msaaccountwflux.domain.AccountEntity;
import org.rauka.dm.msaaccountwflux.service.models.AccountDTO;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "identification", source = "identification")
    @Mapping(target = "accountType", source = "accountType", qualifiedByName = "accountTypeToEnum")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToOffsetDateTime")
    AccountDTO toDto(AccountEntity accountEntity);

    @Mapping(target = "accountId", source = "accountId")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "identification", source = "identification")
    @Mapping(target = "accountType", source = "accountType", qualifiedByName = "enumToAccountType")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToLocalDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToLocalDateTime")
    AccountEntity toEntity(AccountDTO accountDTO);

    @Named("accountTypeToEnum")
    default AccountDTO.AccountTypeEnum accountTypeToEnum(AccountEntity.AccountType accountType) {
        if (accountType == null) return null;
        return AccountDTO.AccountTypeEnum.valueOf(accountType.name());
    }

    @Named("enumToAccountType")
    default AccountEntity.AccountType enumToAccountType(AccountDTO.AccountTypeEnum accountType) {
        if (accountType == null) return null;
        return AccountEntity.AccountType.valueOf(accountType.name());
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
}