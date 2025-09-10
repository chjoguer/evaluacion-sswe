package com.example.ocore.infrastructure.output.adapter;

import com.example.ocore.application.output.port.AccountServicePort;
import com.example.ocore.domain.Account;
import com.example.ocore.infrastructure.dto.AccountDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceAdapter implements AccountServicePort {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.account.base-url}")
    private String accountServiceBaseUrl;

    @Value("${services.account.timeout}")
    private int timeout;

    @Value("${services.account.path}")
    private String path;

    private WebClient getWebClient() {
        return webClientBuilder
                .baseUrl(accountServiceBaseUrl)
                .build();
    }

    @Override
    public Mono<Account> createAccount(Account account) {
        log.info("Creating account: {}",path);
        return getWebClient()
                .post()
                .uri(path)
                .bodyValue(mapToDTO(account))
                .retrieve()
                .bodyToMono(AccountDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to create account", error));
    }

    @Override
    public Mono<Account> getAccountById(Long id) {
        return getWebClient()
                .get()
                .uri(path + "/{id}", id)
                .exchangeToMono(resp -> {
                    if (resp.statusCode() == HttpStatus.NOT_FOUND) return Mono.empty();
                    return resp.bodyToMono(AccountDTO.class);
                })
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to get account by id: " + id, error));
    }

    @Override
    public Flux<Account> getAccountsByClientId(Long clientId) {
        return getWebClient()
                .get()
                .uri(path + "/client/{clientId}", clientId)
                .retrieve()
                .bodyToFlux(AccountDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to get accounts by client id: " + clientId, error));
    }

    @Override
    public Mono<Account> updateAccount(Long id, Account account) {
        return getWebClient()
                .put()
                .uri(path + "/{id}", id)
                .bodyValue(mapToDTO(account))
                .exchangeToMono(resp -> {
                    if (resp.statusCode() == HttpStatus.NOT_FOUND) return Mono.empty();
                    return resp.bodyToMono(AccountDTO.class);
                })
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to update account with id: " + id, error));
    }

    @Override
    public Mono<Void> deleteAccount(Long id) {
        return getWebClient()
                .delete()
                .uri(path + "/{id}", id)
                .exchangeToMono(resp -> {
                    if (resp.statusCode() == HttpStatus.NOT_FOUND) return Mono.empty();
                    return resp.bodyToMono(Void.class);
                })
                .timeout(Duration.ofMillis(timeout))
                .doOnError(error -> log.error("Error calling account service to delete account with id: " + id, error));
    }

    @Override
    public Flux<Account> getAllAccounts() {
        return getWebClient()
                .get()
                .uri(path)
                .retrieve()
                .bodyToFlux(AccountDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to get all accounts", error));
    }

    @Override
    public Mono<Account> getAccountByAccountNumber(String accountNumber) {
        return getWebClient()
                .get()
                .uri(path + "/number/{accountNumber}", accountNumber)
                .exchangeToMono(resp -> {
                    if (resp.statusCode() == HttpStatus.NOT_FOUND) return Mono.empty();
                    return resp.bodyToMono(AccountDTO.class);
                })
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to get by account number: " + accountNumber, error));
    }

    @Override
    public Flux<Account> getAccountsByType(String accountType) {
        return getWebClient()
                .get()
                .uri(path + "/type/{accountType}", accountType)
                .retrieve()
                .bodyToFlux(AccountDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling account service to get by account type: " + accountType, error));
    }

    private AccountDTO mapToDTO(Account account) {
        return AccountDTO.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .identification(account.getIdentification())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    private Account mapToDomain(AccountDTO dto) {
        return Account.builder()
                .id(dto.getAccountId())
                .clientId(null)
                .accountNumber(dto.getAccountNumber())
                .identification(dto.getIdentification())
                .accountType(dto.getAccountType())
                .balance(dto.getBalance())
                .status(null)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
