package com.example.ocore.infrastructure.controller;

import com.example.ocore.application.input.port.AccountUseCase;
import com.example.ocore.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping
    public Mono<ResponseEntity<Account>> createAccount(@RequestBody Account account) {
        log.info("Creating account: {}", account);
        return accountUseCase.createAccount(account)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Account>>> getAllAccounts() {
        log.info("Getting all accounts");
        return Mono.just(ResponseEntity.ok(
                accountUseCase.getAllAccounts()
        ));
    }

    @GetMapping("/client/{clientId}")
    public Mono<ResponseEntity<Flux<Account>>> getAccountsByClientId(@PathVariable Long clientId) {
        log.info("Getting accounts by client id: {}", clientId);
        return Mono.just(ResponseEntity.ok(
                accountUseCase.getAccountsByClientId(clientId)
        ));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Account>> getAccountById(@PathVariable Long id) {
        log.info("Getting account by id: {}", id);
        return accountUseCase.getAccountById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Account>> updateAccount(@PathVariable Long id, @RequestBody Account account) {
        log.info("Updating account with id: {}", id);
        return accountUseCase.updateAccount(id, account)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable Long id) {
        log.info("Deleting account with id: {}", id);
        return accountUseCase.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
}
