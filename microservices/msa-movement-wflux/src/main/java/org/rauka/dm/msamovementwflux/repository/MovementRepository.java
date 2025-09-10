package org.rauka.dm.msamovementwflux.repository;

import org.rauka.dm.msamovementwflux.domain.MovementEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface MovementRepository extends ReactiveCrudRepository<MovementEntity, Long> {

    @Query("SELECT * FROM movements WHERE unique_key = :uniqueKey")
    Mono<MovementEntity> findByUniqueKey(String uniqueKey);

    @Query("SELECT * FROM movements WHERE account_id = :accountId ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByAccountId(Long accountId);

    @Query("SELECT * FROM movements WHERE account_id = :accountId AND occurred_at >= :from AND occurred_at <= :to ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByAccountIdAndOccurredAtBetween(Long accountId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT * FROM movements WHERE movement_type = :movementType ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByMovementType(MovementEntity.MovementType movementType);

    @Query("SELECT * FROM movements WHERE account_id = :accountId AND movement_type = :movementType ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByAccountIdAndMovementType(Long accountId, MovementEntity.MovementType movementType);

    @Query("SELECT * FROM movements WHERE account_id = :accountId AND movement_type = :movementType AND occurred_at >= :from AND occurred_at <= :to ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByAccountIdAndMovementTypeAndOccurredAtBetween(Long accountId, MovementEntity.MovementType movementType, LocalDateTime from, LocalDateTime to);

    @Query("SELECT * FROM movements WHERE occurred_at >= :from AND occurred_at <= :to ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByOccurredAtBetween(LocalDateTime from, LocalDateTime to);

    @Query("SELECT * FROM movements WHERE movement_type = :movementType AND occurred_at >= :from AND occurred_at <= :to ORDER BY occurred_at DESC")
    Flux<MovementEntity> findByMovementTypeAndOccurredAtBetween(MovementEntity.MovementType movementType, LocalDateTime from, LocalDateTime to);
}
