package com.example.ocore.infrastructure.output.adapter;

import com.example.ocore.application.output.port.MovementServicePort;
import com.example.ocore.domain.Movement;
import com.example.ocore.infrastructure.dto.MovementCreateDTO;
import com.example.ocore.infrastructure.dto.MovementDTO;
import com.example.ocore.infrastructure.dto.MovementUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovementServiceAdapter implements MovementServicePort {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.movement.base-url}")
    private String movementServiceBaseUrl;

    @Value("${services.movement.timeout}")
    private int timeout;

    @Value("${services.movement.path:/api/movements}")
    private String path;

    private WebClient getWebClient() {
        return webClientBuilder
                .baseUrl(movementServiceBaseUrl)
                .build();
    }

    @Override
    public Mono<Movement> createMovement(Movement movement) {
        return getWebClient()
                .post()
                .uri(path)
                .bodyValue(mapToCreateDTO(movement))
                .retrieve()
                .bodyToMono(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to create movement", error));
    }

    @Override
    public Mono<Movement> getMovementById(Long id) {
        return getWebClient()
                .get()
                .uri(path + "/{movementId}", id)
                .retrieve()
                .bodyToMono(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to get movement by id: " + id, error));
    }

    @Override
    public Flux<Movement> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParamIfPresent("accountId", accountId != null ? java.util.Optional.of(accountId) : java.util.Optional.empty())
                        .queryParamIfPresent("from", from != null ? java.util.Optional.of(from.toString()) : java.util.Optional.empty())
                        .queryParamIfPresent("to", to != null ? java.util.Optional.of(to.toString()) : java.util.Optional.empty())
                        .queryParamIfPresent("movementType", movementType != null ? java.util.Optional.of(movementType) : java.util.Optional.empty())
                        .build())
                .retrieve()
                .bodyToFlux(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to get all movements", error));
    }

    @Override
    public Mono<Movement> updateMovement(Long id, Movement movement) {
        return getWebClient()
                .put()
                .uri(path + "/{movementId}", id)
                .bodyValue(mapToUpdateDTO(movement))
                .retrieve()
                .bodyToMono(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to update movement with id: " + id, error));
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        return getWebClient()
                .delete()
                .uri(path + "/{movementId}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnError(error -> log.error("Error calling movement service to delete movement with id: " + id, error));
    }

    public Mono<Movement> getMovementByUniqueKey(String uniqueKey) {
        return getWebClient()
                .get()
                .uri(path + "/unique/{uniqueKey}", uniqueKey)
                .retrieve()
                .bodyToMono(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to get movement by unique key: " + uniqueKey, error));
    }

    public Flux<Movement> getMovementsByAccount(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType) {
        return getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path + "/account/{accountId}")
                        .queryParamIfPresent("from", from != null ? java.util.Optional.of(from.toString()) : java.util.Optional.empty())
                        .queryParamIfPresent("to", to != null ? java.util.Optional.of(to.toString()) : java.util.Optional.empty())
                        .queryParamIfPresent("movementType", movementType != null ? java.util.Optional.of(movementType) : java.util.Optional.empty())
                        .build(accountId))
                .retrieve()
                .bodyToFlux(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to get movements by account: " + accountId, error));
    }

    public Flux<Movement> getMovementsByType(String movementType) {
        return getWebClient()
                .get()
                .uri(path + "/type/{movementType}", movementType)
                .retrieve()
                .bodyToFlux(MovementDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling movement service to get movements by type: " + movementType, error));
    }

    private MovementCreateDTO mapToCreateDTO(Movement movement) {
        return MovementCreateDTO.builder()
                .uniqueKey(movement.getUniqueKey())
                .accountId(movement.getAccountId())
                .occurredAt(movement.getOccurredAt())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(movement.getBalance())
                .description(movement.getDescription())
                .reference(movement.getReference())
                .build();
    }

    private MovementUpdateDTO mapToUpdateDTO(Movement movement) {
        return MovementUpdateDTO.builder()
                .description(movement.getDescription())
                .reference(movement.getReference())
                .build();
    }

    private Movement mapToDomain(MovementDTO dto) {
        return Movement.builder()
                .id(dto.getMovementId())
                .uniqueKey(dto.getUniqueKey())
                .accountId(dto.getAccountId())
                .occurredAt(dto.getOccurredAt())
                .movementType(dto.getMovementType())
                .amount(dto.getAmount())
                .balance(dto.getBalance())
                .description(dto.getDescription())
                .reference(dto.getReference())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
