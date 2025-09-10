package com.example.ocore.application.input.port;

import com.example.ocore.domain.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountUseCase {
    Mono<Account> createAccount(Account account);
    Mono<Account> getAccountById(Long id);
    Flux<Account> getAccountsByClientId(Long clientId);
    Mono<Account> updateAccount(Long id, Account account);
    Mono<Void> deleteAccount(Long id);

    Flux<Account> getAllAccounts();
    Mono<Account> getAccountByAccountNumber(String accountNumber);
    Flux<Account> getAccountsByType(String accountType);
}
