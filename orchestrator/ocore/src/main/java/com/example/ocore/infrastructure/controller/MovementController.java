package com.example.ocore.infrastructure.controller;

import com.example.ocore.application.facade.MovementFacade;
import com.example.ocore.application.input.port.MovementUseCase;
import com.example.ocore.domain.Movement;
import com.example.ocore.domain.exception.AccountNotFoundException;
import com.example.ocore.domain.exception.InvalidMovementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementUseCase movementUseCase;
    private final MovementFacade movementFacade;

    @PostMapping
    public Mono<ResponseEntity<Movement>> createMovement(@RequestBody Movement movement) {
        log.info("Creating movement: {}", movement);
        return movementUseCase.createMovement(movement)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/process")
    public Mono<ResponseEntity<Movement>> processMovementWithAccountUpdate(
            @RequestBody Movement movement,
            @RequestParam String accountNumber) {

        log.info("Processing movement with account update for account: {}", accountNumber);

        return movementFacade.createMovementWithAccountUpdate(movement, accountNumber)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
                .onErrorResume(AccountNotFoundException.class,
                        error -> Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(InvalidMovementException.class,
                        error -> Mono.just(ResponseEntity.badRequest().build()))
                .onErrorResume(Exception.class,
                        error -> {
                            log.error("Unexpected error processing movement", error);
                            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                        });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Movement>> getMovementById(@PathVariable Long id) {
        log.info("Getting movement by id: {}", id);
        return movementUseCase.getMovementById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Movement>>> getAllMovements(
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(required = false) String movementType) {

        log.info("Getting all movements with filters - accountId: {}, from: {}, to: {}, type: {}",
                accountId, from, to, movementType);

        return Mono.just(ResponseEntity.ok(
                movementUseCase.getAllMovements(accountId, from, to, movementType)
        ));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Movement>> updateMovement(@PathVariable Long id, @RequestBody Movement movement) {
        log.info("Updating movement with id: {}", id);
        return movementUseCase.updateMovement(id, movement)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMovement(@PathVariable Long id) {
        log.info("Deleting movement with id: {}", id);
        return movementUseCase.deleteMovement(id)
                .map(result -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
