package org.rauka.dm.msaaccountwflux.repository;

import org.rauka.dm.msaaccountwflux.domain.AccountEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {

    @Query("SELECT * FROM accounts WHERE account_number = :accountNumber")
    Mono<AccountEntity> findByAccountNumber(String accountNumber);

    @Query("SELECT * FROM accounts WHERE account_type = :accountType")
    Flux<AccountEntity> findByAccountType(AccountEntity.AccountType accountType);
}
