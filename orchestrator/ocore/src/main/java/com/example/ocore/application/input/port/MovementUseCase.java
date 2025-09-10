package com.example.ocore.application.input.port;

import com.example.ocore.domain.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface MovementUseCase {
    Mono<Movement> createMovement(Movement movement);
    Mono<Movement> getMovementById(Long id);
    Flux<Movement> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType);
    Mono<Movement> updateMovement(Long id, Movement movement);
    Mono<Void> deleteMovement(Long id);
}
