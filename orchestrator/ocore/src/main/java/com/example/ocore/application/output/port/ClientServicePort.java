package com.example.ocore.application.output.port;

import com.example.ocore.domain.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientServicePort {
    Mono<Client> createClient(Client client);
    Mono<Client> getClientById(String id);
    Flux<Client> getAllClients();
    Mono<Client> updateClient(String id, Client client);
    Mono<Void> deleteClient(String id);
}
