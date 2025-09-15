package org.rauka.dm.msapdfgenerator.repository;

import org.rauka.dm.msapdfgenerator.domain.MovementEntity;
import org.rauka.dm.msapdfgenerator.dto.MovementReportDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;


@Repository
public interface MovementRepository extends ReactiveCrudRepository<MovementEntity, Long> {

    @Query(value = "SELECT m.occurred_at , " +
                   "       m.description, " +
                   "       m.account_id, " +
                   "       m.movement_type, " +
                   "       'PROCESSED' as status, " +
                   "       (m.balance - m.amount) as initialAmount, " +
                   "       m.amount, " +
                   "       m.balance , " +
                   "       m.reference, " +
                   "       m.created_at " +
                   "FROM movements m " +
                   "WHERE m.account_id = :accountId " +
                   "  AND DATE(m.created_at) BETWEEN :startDate AND :endDate " +
                   "ORDER BY m.occurred_at DESC")
    Flux<MovementReportDTO> findMovementReportByAccountAndDate(Long accountId,
                                                               LocalDate startDate,
                                                               LocalDate endDate);

    @Query(value = "SELECT m.occurred_at, " +
                   "       m.description, " +
                   "       m.account_id, " +
                   "       m.movement_type, " +
                   "       'PROCESSED' as status, " +
                   "       (m.balance - m.amount) as initialAmount, " +
                   "       m.amount, " +
                   "       m.balance, " +
                   "       m.reference, " +
                   "       m.created_at " +
                   "FROM movements m " +
                   "WHERE m.account_id = '27' and DATE(m.created_at) BETWEEN :startDate AND :endDate " +
                   "ORDER BY m.occurred_at DESC")
    Flux<MovementReportDTO> findAllMovementReportByDate(LocalDate startDate, LocalDate endDate);
}
