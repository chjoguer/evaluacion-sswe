package com.example.ocore.application.service;

import com.example.ocore.application.input.port.AccountUseCase;
import com.example.ocore.application.output.port.AccountServicePort;
import com.example.ocore.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {

    private final AccountServicePort accountServicePort;

    @Override
    public Mono<Account> createAccount(Account account) {
        log.info("Creating account: {}", account);
        return accountServicePort.createAccount(account)
                .doOnSuccess(result -> log.info("Account created successfully: {}", result))
                .doOnError(error -> log.error("Error creating account", error));
    }

    @Override
    public Mono<Account> getAccountById(Long id) {
        log.info("Getting account by id: {}", id);
        return accountServicePort.getAccountById(id)
                .doOnSuccess(result -> log.info("Account found: {}", result))
                .doOnError(error -> log.error("Error getting account by id: {}", id, error));
    }

    @Override
    public Flux<Account> getAccountsByClientId(Long clientId) {
        log.info("Getting accounts by client id: {}", clientId);
        return accountServicePort.getAccountsByClientId(clientId)
                .doOnNext(account -> log.debug("Retrieved account: {}", account))
                .doOnError(error -> log.error("Error getting accounts by client id: {}", clientId, error));
    }

    @Override
    public Mono<Account> updateAccount(Long id, Account account) {
        log.info("Updating account with id: {}", id);
        return accountServicePort.updateAccount(id, account)
                .doOnSuccess(result -> log.info("Account updated successfully: {}", result))
                .doOnError(error -> log.error("Error updating account with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        return accountServicePort.deleteAccount(id)
                .doOnSuccess(result -> log.info("Account deleted successfully"))
                .doOnError(error -> log.error("Error deleting account with id: {}", id, error));
    }

    @Override
    public Flux<Account> getAllAccounts() {
        log.info("Getting all accounts");
        return accountServicePort.getAllAccounts()
                .doOnNext(acc -> log.debug("Account: {}", acc))
                .doOnError(error -> log.error("Error getting all accounts", error));
    }

    @Override
    public Mono<Account> getAccountByAccountNumber(String accountNumber) {
        log.info("Getting account by number: {}", accountNumber);
        return accountServicePort.getAccountByAccountNumber(accountNumber)
                .doOnSuccess(acc -> log.info("Account found by number: {}", acc))
                .doOnError(error -> log.error("Error getting account by number: {}", accountNumber, error));
    }

    @Override
    public Flux<Account> getAccountsByType(String accountType) {
        log.info("Getting accounts by type: {}", accountType);
        return accountServicePort.getAccountsByType(accountType)
                .doOnNext(acc -> log.debug("Account by type: {}", acc))
                .doOnError(error -> log.error("Error getting accounts by type: {}", accountType, error));
    }
}
