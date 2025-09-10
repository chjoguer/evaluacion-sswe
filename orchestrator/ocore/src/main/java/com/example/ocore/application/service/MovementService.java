package com.example.ocore.application.service;

import com.example.ocore.application.input.port.MovementUseCase;
import com.example.ocore.application.output.port.MovementServicePort;
import com.example.ocore.domain.Movement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovementService implements MovementUseCase {

    private final MovementServicePort movementServicePort;

    @Override
    public Mono<Movement> createMovement(Movement movement) {
        log.info("Creating movement: {}", movement);
        return movementServicePort.createMovement(movement)
                .doOnSuccess(result -> log.info("Movement created successfully: {}", result))
                .doOnError(error -> log.error("Error creating movement", error));
    }

    @Override
    public Mono<Movement> getMovementById(Long id) {
        log.info("Getting movement by id: {}", id);
        return movementServicePort.getMovementById(id)
                .doOnSuccess(result -> log.info("Movement found: {}", result))
                .doOnError(error -> log.error("Error getting movement by id: {}", id, error));
    }

    @Override
    public Flux<Movement> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType) {
        log.info("Getting all movements with filters - accountId: {}, from: {}, to: {}, type: {}", 
                accountId, from, to, movementType);
        return movementServicePort.getAllMovements(accountId, from, to, movementType)
                .doOnNext(movement -> log.debug("Retrieved movement: {}", movement))
                .doOnError(error -> log.error("Error getting movements", error));
    }

    @Override
    public Mono<Movement> updateMovement(Long id, Movement movement) {
        log.info("Updating movement with id: {}", id);
        return movementServicePort.updateMovement(id, movement)
                .doOnSuccess(result -> log.info("Movement updated successfully: {}", result))
                .doOnError(error -> log.error("Error updating movement with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        log.info("Deleting movement with id: {}", id);
        return movementServicePort.deleteMovement(id)
                .doOnSuccess(result -> log.info("Movement deleted successfully"))
                .doOnError(error -> log.error("Error deleting movement with id: {}", id, error));
    }
}
