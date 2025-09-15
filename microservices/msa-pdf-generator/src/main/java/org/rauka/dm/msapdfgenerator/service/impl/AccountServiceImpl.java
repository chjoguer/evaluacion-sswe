package org.rauka.dm.msapdfgenerator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msapdfgenerator.dto.AccountDTO;
import org.rauka.dm.msapdfgenerator.service.AccountService;
import org.rauka.dm.msapdfgenerator.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final WebClient webClient;

    @Value("${account.api.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public Mono<AccountDTO> getAccountById(Long accountId) {
        log.debug("Fetching account with ID: {}", accountId);

        return webClient.get()
            .uri(baseUrl + "/api/accounts/{accountId}", accountId)
            .headers(headers -> headers.set("Accept", "application/json"))
            .retrieve()
            .bodyToMono(AccountDTO.class)
            .doOnSuccess(account -> log.debug("Successfully retrieved account: {}", account.getAccountNumber()))
            .doOnError(error -> log.error("Error fetching account with ID {}: {}",
                accountId, error.getMessage()))
            .onErrorResume(WebClientResponseException.NotFound.class,
                ex -> {
                    log.warn("Account not found with ID: {}", accountId);
                    return Mono.empty();
                })
            .onErrorResume(Exception.class,
                ex -> {
                    log.error("Unexpected error fetching account: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch account", ex));
                });
    }
}
