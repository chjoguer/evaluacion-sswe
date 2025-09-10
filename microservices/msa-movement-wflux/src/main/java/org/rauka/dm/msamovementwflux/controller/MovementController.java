package org.rauka.dm.msamovementwflux.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msamovementwflux.service.MovementService;
import org.rauka.dm.msamovementwflux.service.models.MovementCreateDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementType;
import org.rauka.dm.msamovementwflux.service.models.MovementUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MovementController implements MovementsApi {

    private final MovementService movementService;

    @Override
    public Mono<ResponseEntity<MovementDTO>> createMovement(Mono<MovementCreateDTO> movementCreateDTO, ServerWebExchange exchange) {
        log.info("Creating movement");
        return movementCreateDTO
                .flatMap(dto -> {
                    log.info("Creating movement: {}", dto);
                    return movementService.createMovement(dto);
                })
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .doOnSuccess(response -> log.info("Movement created successfully"))
                .doOnError(error -> log.error("Error creating movement", error))
                .onErrorReturn(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementDTO>>> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, MovementType movementType, ServerWebExchange exchange) {
        log.info("Getting all movements with filters - accountId: {}, from: {}, to: {}, type: {}", accountId, from, to, movementType);
        String movementTypeStr = movementType != null ? movementType.toString() : null;
        return Mono.just(ResponseEntity.ok(
                movementService.getAllMovements(accountId, from, to, movementTypeStr)
                        .doOnNext(movement -> log.debug("Retrieved movement: {}", movement))
        ));
    }

    @Override
    public Mono<ResponseEntity<MovementDTO>> getMovementById(Long movementId, ServerWebExchange exchange) {
        log.info("Getting movement by ID: {}", movementId);
        return movementService.getMovementById(movementId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Movement retrieved for ID: {}", movementId))
                .doOnError(error -> log.error("Error retrieving movement with ID: {}", movementId, error));
    }

    @Override
    public Mono<ResponseEntity<MovementDTO>> getMovementByUniqueKey(String uniqueKey, ServerWebExchange exchange) {
        log.info("Getting movement by unique key: {}", uniqueKey);
        return movementService.getMovementByUniqueKey(uniqueKey)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Movement retrieved for unique key: {}", uniqueKey))
                .doOnError(error -> log.error("Error retrieving movement with unique key: {}", uniqueKey, error));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementDTO>>> getMovementsByAccount(Long accountId, OffsetDateTime from, OffsetDateTime to, MovementType movementType, ServerWebExchange exchange) {
        log.info("Getting movements by account: {} with filters - from: {}, to: {}, type: {}", accountId, from, to, movementType);
        String movementTypeStr = movementType != null ? movementType.toString() : null;
        return Mono.just(ResponseEntity.ok(
                movementService.getMovementsByAccount(accountId, from, to, movementTypeStr)
                        .doOnNext(movement -> log.debug("Retrieved movement by account: {}", movement))
        ));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementDTO>>> getMovementsByType(MovementType movementType, ServerWebExchange exchange) {
        log.info("Getting movements by type: {}", movementType);
        String movementTypeStr = movementType != null ? movementType.toString() : null;
        return Mono.just(ResponseEntity.ok(
                movementService.getMovementsByType(movementTypeStr)
                        .doOnNext(movement -> log.debug("Retrieved movement by type: {}", movement))
        ));
    }

    @Override
    public Mono<ResponseEntity<MovementDTO>> updateMovement(Long movementId, Mono<MovementUpdateDTO> movementUpdateDTO, ServerWebExchange exchange) {
        log.info("Updating movement ID: {}", movementId);
        return movementUpdateDTO
                .flatMap(dto -> {
                    log.info("Updating movement ID: {} with data: {}", movementId, dto);
                    return movementService.updateMovement(movementId, dto);
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Movement updated successfully for ID: {}", movementId))
                .doOnError(error -> log.error("Error updating movement with ID: {}", movementId, error));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(Long movementId, ServerWebExchange exchange) {
        log.info("Deleting movement ID: {}", movementId);
        return movementService.deleteMovement(movementId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Movement deleted successfully for ID: {}", movementId))
                .doOnError(error -> log.error("Error deleting movement with ID: {}", movementId, error));
    }
}
