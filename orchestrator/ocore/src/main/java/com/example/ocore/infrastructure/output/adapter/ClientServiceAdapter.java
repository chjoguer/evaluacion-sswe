package com.example.ocore.infrastructure.output.adapter;

import com.example.ocore.application.output.port.ClientServicePort;
import com.example.ocore.domain.Client;
import com.example.ocore.infrastructure.dto.ClientDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientServiceAdapter implements ClientServicePort {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.client.base-url}")
    private String clientServiceBaseUrl;

    @Value("${services.client.timeout}")
    private int timeout;

    private WebClient getWebClient() {
        return webClientBuilder
                .baseUrl(clientServiceBaseUrl)
                .build();
    }

    @Value("${services.client.path}")
    private String path;

    @Override
    public Mono<Client> createClient(Client client) {
        return getWebClient()
                .post()
                .uri(path)
                .bodyValue(mapToDTO(client))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Client created successfully with status: {}", response.statusCode());
                        return Mono.just(client.toBuilder()
                                .createdAt(OffsetDateTime.now())
                                .updatedAt(OffsetDateTime.now())
                                .build());
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .timeout(Duration.ofMillis(timeout))
                .doOnError(error -> log.error("Error calling client service to create client", error));
    }

    @Override
    public Mono<Client> getClientById(String id) {
        return getWebClient()
                .get()
                .uri(path +"/{id}", id)
                .retrieve()
                .bodyToMono(ClientDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling client service to get client by id: {}", id, error));
    }

    @Override
    public Flux<Client> getAllClients() {
        return getWebClient()
                .get()
                .uri(path)
                .retrieve()
                .bodyToFlux(ClientDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling client service to get all clients", error));
    }

    @Override
    public Mono<Client> updateClient(String id, Client client) {
        return getWebClient()
                .put()
                .uri(path+"/{id}", id)
                .bodyValue(mapToDTO(client))
                .retrieve()
                .bodyToMono(ClientDTO.class)
                .timeout(Duration.ofMillis(timeout))
                .map(this::mapToDomain)
                .doOnError(error -> log.error("Error calling client service to update client with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteClient(String id) {
        return getWebClient()
                .delete()
                .uri(path + "/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnError(error -> log.error("Error calling client service to delete client with id: {}", id, error));
    }

    private ClientDTO mapToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .direction(client.getDirection())
                .cellphone(client.getCellphone())
                .password(client.getPassword())
                .status(client.getStatus())
                .documentType(client.getDocumentType())
                .identification(client.getIdentification())
                .build();
    }

    private Client mapToDomain(ClientDTO dto) {
        return Client.builder()
                .id(dto.getId())
                .fullName(dto.getFullName())
                .direction(dto.getDirection())
                .cellphone(dto.getCellphone())
                .password(dto.getPassword())
                .status(dto.getStatus())
                .documentType(dto.getDocumentType())
                .identification(dto.getIdentification())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
