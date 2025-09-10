package com.example.ocore.infrastructure.controller;

import com.example.ocore.application.facade.ClientFacade;
import com.example.ocore.domain.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientFacade clientFacade;

    @PostMapping
    public Mono<ResponseEntity<Client>> createClient(@RequestBody Client client) {
        log.info("Creating client with default account: {}", client.getFullName());
        return clientFacade.createClientWithDefaultAccount(client)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Client>> getClientById(@PathVariable String id) {
        log.info("Getting client by id: {}", id);
        return clientFacade.getClientById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Client>>> getAllClients() {
        log.info("Getting all clients");
        return Mono.just(ResponseEntity.ok(
                clientFacade.getAllClients()
        ));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Client>> updateClient(@PathVariable String id, @RequestBody Client client) {
        log.info("Updating client with id: {}", id);
        return clientFacade.updateClient(id, client)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClient(@PathVariable String id) {
        log.info("Deleting client with id: {}", id);
        return clientFacade.deleteClient(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
