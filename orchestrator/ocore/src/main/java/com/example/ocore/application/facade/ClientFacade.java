package com.example.ocore.application.facade;

import com.example.ocore.application.input.port.AccountUseCase;
import com.example.ocore.application.input.port.ClientUseCase;
import com.example.ocore.domain.Account;
import com.example.ocore.domain.Client;
import com.example.ocore.domain.exception.AccountCreationException;
import com.example.ocore.domain.exception.ClientCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientFacade {

    private final ClientUseCase clientUseCase;
    private final AccountUseCase accountUseCase;
    private final Random random = new Random();

    public Mono<Client> createClientWithDefaultAccount(Client client) {
        log.info("Creating client with default account for: {}", client.getFullName());

        return clientUseCase.createClient(client)
                .doOnSuccess(createdClient -> log.info("Client created successfully with ID: {}", createdClient))
                .onErrorMap(throwable -> {
                    log.error("Error creating client: {}", throwable.getMessage());
                    return new ClientCreationException("Failed to create client: " + throwable.getMessage(), throwable);
                })
                .flatMap(createdClient -> {
                    log.info("Starting account creation for client identification: {}", client.getIdentification());
                    return createDefaultAccount(client)
                            .doOnSuccess(account -> log.info("Account created successfully: {}", account))
                            .onErrorMap(throwable -> {
                                log.error("Error creating default account for client {}: {}", client.getIdentification(), throwable.getMessage());
                                return new AccountCreationException("Failed to create default account for client " + client.getIdentification(), throwable);
                            })
                            .map(account -> client);
                })
                .doOnSuccess(result -> log.info("Client and default account created successfully for: {}", client.getFullName()))
                .doOnError(error -> log.error("Error in complete flow for client: {}", client.getFullName(), error));
    }

    private Mono<Account> createDefaultAccount(Client client) {
        log.info("Creating default account for: {}", client.getFullName());

        String accountNumber = generateAccountNumber();

        Account defaultAccount = Account.builder()
                .accountNumber(accountNumber)
                .identification(client.getIdentification())
                .accountType("SAVINGS")
                .balance(BigDecimal.ZERO)
                .clientId(null)
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        log.info("Creating default savings account {} ", defaultAccount);
        return accountUseCase.createAccount(defaultAccount);
    }

    private String generateAccountNumber() {
        int sixDigitNumber = 100000 + random.nextInt(900000);
        return "ACC-2024-" + String.format("%04d", sixDigitNumber % 10000);
    }

    public Mono<Client> getClientById(String id) {
        return clientUseCase.getClientById(id);
    }

    public reactor.core.publisher.Flux<Client> getAllClients() {
        return clientUseCase.getAllClients();
    }

    public Mono<Client> updateClient(String id, Client client) {
        return clientUseCase.updateClient(id, client);
    }

    public Mono<Void> deleteClient(String id) {
        return clientUseCase.deleteClient(id);
    }
}
