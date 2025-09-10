package org.rauka.dm.msaaccountwflux.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msaaccountwflux.service.AccountService;
import org.rauka.dm.msaaccountwflux.service.models.AccountDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<AccountDTO>> createAccount(Mono<AccountDTO> accountDTO, ServerWebExchange exchange) {
        log.info("Creating account");
        return accountDTO
                .flatMap(dto -> {
                    log.info("Creating account: {}", dto);
                    return accountService.createAccount(dto);
                })
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .doOnSuccess(response -> log.info("Account created successfully"))
                .doOnError(error -> log.error("Error creating account", error));
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountDTO>>> getAllAccounts(ServerWebExchange exchange) {
        log.info("Getting all accounts");
        return Mono.just(ResponseEntity.ok(
                accountService.getAllAccounts()
                        .doOnNext(account -> log.debug("Retrieved account: {}", account))
        ));
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> getAccountById(Long accountId, ServerWebExchange exchange) {
        log.info("Getting account by ID: {}", accountId);
        return accountService.getAccountById(accountId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Account retrieved for ID: {}", accountId))
                .doOnError(error -> log.error("Error retrieving account with ID: {}", accountId, error));
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> updateAccount(Long accountId, Mono<AccountDTO> accountDTO, ServerWebExchange exchange) {
        log.info("Updating account ID: {}", accountId);
        return accountDTO
                .flatMap(dto -> {
                    log.info("Updating account ID: {} with data: {}", accountId, dto);
                    return accountService.updateAccount(accountId, dto);
                })
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Account updated successfully for ID: {}", accountId))
                .doOnError(error -> log.error("Error updating account with ID: {}", accountId, error));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(Long accountId, ServerWebExchange exchange) {
        log.info("Deleting account ID: {}", accountId);
        return accountService.deleteAccount(accountId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Account deleted successfully for ID: {}", accountId))
                .doOnError(error -> log.error("Error deleting account with ID: {}", accountId, error));
    }

    @Override
    public Mono<ResponseEntity<AccountDTO>> getAccountByAccountNumber(String accountNumber, ServerWebExchange exchange) {
        log.info("Getting account by account number: {}", accountNumber);
        return accountService.getAccountByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .doOnSuccess(response -> log.info("Account retrieved for account number: {}", accountNumber))
                .doOnError(error -> log.error("Error retrieving account with account number: {}", accountNumber, error));
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountDTO>>> getAccountsByType(String accountType, ServerWebExchange exchange) {
        log.info("Getting accounts by type: {}", accountType);
        return Mono.just(ResponseEntity.ok(
                accountService.getAccountsByType(accountType)
                        .doOnNext(account -> log.debug("Retrieved account by type: {}", account))
        ));
    }
}
