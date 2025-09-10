package org.rauka.dm.msamovementwflux.service;

import org.rauka.dm.msamovementwflux.service.models.MovementCreateDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementDTO;
import org.rauka.dm.msamovementwflux.service.models.MovementUpdateDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface MovementService {
    
    Mono<MovementDTO> createMovement(MovementCreateDTO movementCreateDTO);
    
    Mono<MovementDTO> getMovementById(Long movementId);
    
    Mono<MovementDTO> getMovementByUniqueKey(String uniqueKey);
    
    Flux<MovementDTO> getAllMovements(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType);
    
    Flux<MovementDTO> getMovementsByAccount(Long accountId, OffsetDateTime from, OffsetDateTime to, String movementType);
    
    Flux<MovementDTO> getMovementsByType(String movementType);
    
    Mono<MovementDTO> updateMovement(Long movementId, MovementUpdateDTO movementUpdateDTO);
    
    Mono<Void> deleteMovement(Long movementId);
}
