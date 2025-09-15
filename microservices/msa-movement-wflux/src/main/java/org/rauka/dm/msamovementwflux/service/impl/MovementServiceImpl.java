package org.rauka.dm.msamovementwflux.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msamovementwflux.domain.MovementEntity;
import org.rauka.dm.msamovementwflux.repository.MovementRepository;
import org.rauka.dm.msamovementwflux.service.MovementService;
import org.rauka.dm.msamovementwflux.service.mapper.MovementMapper;
import org.rauka.dm.msamovementwflux.service.models.MovementCreateDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementUpdateDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;
    private final MovementMapper movementMapper;

    @Override
    public Mono<MovementDTO> createMovement(MovementCreateDTO movementCreateDTO) {
        log.info("Service: Creating new movement: {}", movementCreateDTO);
        
        return movementRepository.findByUniqueKey(movementCreateDTO.getUniqueKey())
                .flatMap(existingMovement -> {
                    log.warn("Movement with unique key {} already exists", movementCreateDTO.getUniqueKey());
                    return Mono.error(new RuntimeException("Movement with unique key already exists"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    MovementEntity entity = movementMapper.createDtoToEntity(movementCreateDTO);
                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());
                    
                    return movementRepository.save(entity)
                            .map(movementMapper::toDto)
                            .doOnSuccess(savedMovement -> log.debug("Movement created successfully: {}", savedMovement))
                            .doOnError(error -> log.error("Error creating movement: {}", error.getMessage()));
                }))
                .cast(MovementDTO.class);
    }

    @Override
    public Mono<MovementDTO> getMovementById(Long movementId) {
        log.info("Service: Fetching movement by ID: {}", movementId);
        
        return movementRepository.findById(movementId)
                .map(movementMapper::toDto)
                .doOnSuccess(movement -> log.debug("Movement found: {}", movement))
                .doOnError(error -> log.error("Error fetching movement by ID {}: {}", movementId, error.getMessage()));
    }

    @Override
    public Mono<MovementDTO> getMovementByUniqueKey(String uniqueKey) {
        log.info("Service: Fetching movement by unique key: {}", uniqueKey);
        
        return movementRepository.findByUniqueKey(uniqueKey)
                .map(movementMapper::toDto)
                .doOnSuccess(movement -> log.debug("Movement found by unique key: {}", movement))
                .doOnError(error -> log.error("Error fetching movement by unique key {}: {}", uniqueKey, error.getMessage()));
    }

    @Override
    public Flux<MovementDTO> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType) {
        log.info("Service: Fetching all movements with filters - accountId: {}, from: {}, to: {}, type: {}", accountId, from, to, movementType);
        
        LocalDateTime fromLocal = from != null ? from.toLocalDateTime() : null;
        LocalDateTime toLocal = to != null ? to.toLocalDateTime() : null;
        MovementEntity.MovementType typeEnum = movementType != null ? MovementEntity.MovementType.valueOf(movementType.toUpperCase()) : null;
        
        Flux<MovementEntity> movements;
        
        if (accountId != null && fromLocal != null && toLocal != null && typeEnum != null) {
            movements = movementRepository.findByAccountIdAndMovementTypeAndOccurredAtBetween(accountId, typeEnum, fromLocal, toLocal);
        } else if (accountId != null && fromLocal != null && toLocal != null) {
            movements = movementRepository.findByAccountIdAndOccurredAtBetween(accountId, fromLocal, toLocal);
        } else if (accountId != null && typeEnum != null) {
            movements = movementRepository.findByAccountIdAndMovementType(accountId, typeEnum);
        } else if (accountId != null) {
            movements = movementRepository.findByAccountId(accountId);
        } else if (fromLocal != null && toLocal != null && typeEnum != null) {
            movements = movementRepository.findByMovementTypeAndOccurredAtBetween(typeEnum, fromLocal, toLocal);
        } else if (fromLocal != null && toLocal != null) {
            movements = movementRepository.findByOccurredAtBetween(fromLocal, toLocal);
        } else if (typeEnum != null) {
            movements = movementRepository.findByMovementType(typeEnum);
        } else {
            movements = movementRepository.findAll();
        }
        
        return movements
                .map(movementMapper::toDto)
                .doOnComplete(() -> log.debug("All movements fetched successfully"))
                .doOnError(error -> log.error("Error fetching all movements: {}", error.getMessage()));
    }

    @Override
    public Flux<MovementDTO> getMovementsByAccount(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType) {
        log.info("Service: Fetching movements by account: {} with filters - from: {}, to: {}, type: {}", accountId, from, to, movementType);
        
        return getAllMovements(accountId, from, to, movementType);
    }

    @Override
    public Flux<MovementDTO> getMovementsByType(String movementType) {
        log.info("Service: Fetching movements by type: {}", movementType);
        
        MovementEntity.MovementType type = MovementEntity.MovementType.valueOf(movementType.toUpperCase());
        
        return movementRepository.findByMovementType(type)
                .map(movementMapper::toDto)
                .doOnComplete(() -> log.debug("Movements fetched successfully by type: {}", movementType))
                .doOnError(error -> log.error("Error fetching movements by type {}: {}", movementType, error.getMessage()));
    }

    @Override
    public Mono<MovementDTO> updateMovement(Long movementId, MovementUpdateDTO movementUpdateDTO) {
        log.info("Service: Updating movement with ID: {}", movementId);
        
        return movementRepository.findById(movementId)
                .flatMap(existingMovement -> {
                    // Only update description and reference as per the UpdateDTO
                    if (movementUpdateDTO.getDescription() != null) {
                        existingMovement.setDescription(movementUpdateDTO.getDescription());
                    }
                    if (movementUpdateDTO.getReference() != null) {
                        existingMovement.setReference(movementUpdateDTO.getReference());
                    }
                    existingMovement.setUpdatedAt(LocalDateTime.now());
                    
                    return movementRepository.save(existingMovement);
                })
                .map(movementMapper::toDto)
                .doOnSuccess(updatedMovement -> log.debug("Movement updated successfully: {}", updatedMovement))
                .doOnError(error -> log.error("Error updating movement {}: {}", movementId, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteMovement(Long movementId) {
        log.info("Service: Deleting movement with ID: {}", movementId);
        
        return movementRepository.deleteById(movementId)
                .doOnSuccess(result -> log.debug("Movement deleted successfully: {}", movementId))
                .doOnError(error -> log.error("Error deleting movement {}: {}", movementId, error.getMessage()));
    }
}
