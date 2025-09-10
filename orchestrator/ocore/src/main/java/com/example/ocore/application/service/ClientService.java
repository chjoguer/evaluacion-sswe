package com.example.ocore.application.service;

import com.example.ocore.application.input.port.ClientUseCase;
import com.example.ocore.application.output.port.ClientServicePort;
import com.example.ocore.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService implements ClientUseCase {

    private final ClientServicePort clientServicePort;

    @Override
    public Mono<Client> createClient(Client client) {
        log.info("Creating client: {}", client);
        return clientServicePort.createClient(client)
                .doOnSuccess(result -> log.info("Client created successfully: {}", result))
                .doOnError(error -> log.error("Error creating client", error));
    }

    @Override
    public Mono<Client> getClientById(String id) {
        log.info("Getting client by id: {}", id);
        return clientServicePort.getClientById(id)
                .doOnSuccess(result -> log.info("Client found: {}", result))
                .doOnError(error -> log.error("Error getting client by id: {}", id, error));
    }

    @Override
    public Flux<Client> getAllClients() {
        log.info("Getting all clients");
        return clientServicePort.getAllClients()
                .doOnNext(client -> log.debug("Retrieved client: {}", client))
                .doOnError(error -> log.error("Error getting all clients", error));
    }

    @Override
    public Mono<Client> updateClient(String id, Client client) {
        log.info("Updating client with id: {}", id);
        return clientServicePort.updateClient(id, client)
                .doOnSuccess(result -> log.info("Client updated successfully: {}", result))
                .doOnError(error -> log.error("Error updating client with id: {}", id, error));
    }

    @Override
    public Mono<Void> deleteClient(String id) {
        log.info("Deleting client with id: {}", id);
        return clientServicePort.deleteClient(id)
                .doOnSuccess(result -> log.info("Client deleted successfully"))
                .doOnError(error -> log.error("Error deleting client with id: {}", id, error));
    }
}
