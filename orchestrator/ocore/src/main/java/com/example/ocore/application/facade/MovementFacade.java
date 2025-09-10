package com.example.ocore.application.facade;

import com.example.ocore.application.input.port.AccountUseCase;
import com.example.ocore.application.input.port.MovementUseCase;
import com.example.ocore.domain.Account;
import com.example.ocore.domain.Movement;
import com.example.ocore.domain.exception.AccountNotFoundException;
import com.example.ocore.domain.exception.InvalidMovementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovementFacade {

    private final MovementUseCase movementUseCase;
    private final AccountUseCase accountUseCase;

    public Mono<Movement> createMovementWithAccountUpdate(Movement movement, String accountNumber) {
        log.info("Processing movement for account: {}", accountNumber);

        return findAccount(accountNumber)
                .flatMap(account -> processMovement(movement, account))
                .doOnSuccess(result -> log.info("Movement processed successfully"))
                .doOnError(error -> log.error("Error processing movement for account: {}", accountNumber, error));
    }

    private Mono<Account> findAccount(String accountNumber) {
        return accountUseCase.getAccountByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountNumber)));
    }

    private Mono<Movement> processMovement(Movement movement, Account account) {
        validateMovement(movement, account);

        BigDecimal newBalance = calculateNewBalance(movement, account);
        Movement enrichedMovement = enrichMovement(movement, account, newBalance);
        Account updatedAccount = updateAccountBalance(account, newBalance);

        return movementUseCase.createMovement(enrichedMovement)
                .flatMap(createdMovement ->
                    accountUseCase.updateAccount(account.getId(), updatedAccount)
                        .thenReturn(createdMovement));
    }

    private void validateMovement(Movement movement, Account account) {
        if (movement.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new InvalidMovementException("Amount cannot be zero");
        }

        if (isWithdrawal(movement) && hasInsufficientFunds(movement, account)) {
            throw new InvalidMovementException(
                String.format("Insufficient funds. Available: %s, Requested: %s",
                    account.getBalance(), movement.getAmount().abs()));
        }
    }

    private BigDecimal calculateNewBalance(Movement movement, Account account) {
        return account.getBalance().add(movement.getAmount());
    }

    private Movement enrichMovement(Movement movement, Account account, BigDecimal newBalance) {
        return Movement.builder()
                .id(movement.getId())
                .uniqueKey(movement.getUniqueKey())
                .accountId(account.getId())
                .occurredAt(OffsetDateTime.now())
                .movementType(movement.getMovementType())
                .amount(movement.getAmount())
                .balance(newBalance)
                .description(movement.getDescription())
                .reference(movement.getReference())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private Account updateAccountBalance(Account account, BigDecimal newBalance) {
        return Account.builder()
                .id(account.getId())
                .clientId(account.getClientId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(newBalance)
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private boolean isWithdrawal(Movement movement) {
        return movement.getAmount().compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean hasInsufficientFunds(Movement movement, Account account) {
        return account.getBalance().compareTo(BigDecimal.ZERO) == 0 ||
               movement.getAmount().abs().compareTo(account.getBalance()) > 0;
    }
}
