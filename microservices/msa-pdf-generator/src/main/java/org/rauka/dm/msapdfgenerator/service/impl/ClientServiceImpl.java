package org.rauka.dm.msapdfgenerator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msapdfgenerator.dto.ClienteDTO;
import org.rauka.dm.msapdfgenerator.service.ClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final WebClient webClient;

    @Value("${cliente.api.base-url:http://localhost:8081}")
    private String baseUrl;

    @Override
    public Mono<ClienteDTO> getClienteByIdentificacion(String identificacion) {
        log.debug("Fetching client with identificacion: {}", identificacion);

        return webClient.get()
            .uri(baseUrl + "/api/clientes/{identificacion}", identificacion)
            .headers(headers -> headers.set("Accept", "application/json"))
            .retrieve()
            .bodyToMono(ClienteDTO.class)
            .doOnSuccess(cliente -> log.debug("Successfully retrieved client: {}", cliente.getFullName()))
            .doOnError(error -> log.error("Error fetching client with identificacion {}: {}",
                identificacion, error.getMessage()))
            .onErrorResume(WebClientResponseException.NotFound.class,
                ex -> {
                    log.warn("Client not found with identificacion: {}", identificacion);
                    return Mono.empty();
                })
            .onErrorResume(Exception.class,
                ex -> {
                    log.error("Unexpected error fetching client: {}", ex.getMessage());
                    return Mono.error(new RuntimeException("Failed to fetch client", ex));
                });
    }
}
