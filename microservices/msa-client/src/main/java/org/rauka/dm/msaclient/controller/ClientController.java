package org.rauka.dm.msaclient.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msaclient.service.ClientService;
import org.rauka.dm.msaclient.service.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController implements ClientesApi {

    private final ClientService clientService;

    @Override
    @GetMapping
    public ResponseEntity<List<Cliente>> clientesGet() {
        log.info("Listing all clients");

        try {
            List<Cliente> clients = clientService.findAll();
            log.debug("Found {} clients", clients.size());
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            log.error("Error listing clients", e);
            throw e;
        }
    }

    @Override
    @PostMapping
    public ResponseEntity<Void> clientesPost(@Valid @RequestBody ClienteCreate clienteCreate) {
        log.info("Creating new client with document: {}", clienteCreate.getIdentification());

        try {
            Cliente createdClient = clientService.createClient(clienteCreate);
            log.info("Client created successfully with identification: {}", createdClient.getIdentification());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            log.warn("Client creation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error creating client", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Cliente> clientesIdGet(@PathVariable String id) {
        log.info("Getting client by ID: {}", id);

        try {
            return clientService.findByIdentification(id.toString())
                    .map(cliente -> {
                        log.debug("Client found: {}", cliente.getIdentification());
                        return ResponseEntity.ok(cliente);
                    })
                    .orElseGet(() -> {
                        log.warn("Client not found with ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            log.error("Error getting client by ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Void> clientesIdPut(@PathVariable String id, @Valid @RequestBody ClienteCreate clienteCreate) {
        log.info("Updating client with ID: {}", id);

        try {
            Cliente updatedClient = clientService.updateClient(clienteCreate, id.toString());
            log.info("Client updated successfully");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Client update failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating client", e);
            throw e;
        }
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Void> clientesIdDelete(@PathVariable String id) {
        log.info("Deleting client with ID: {}", id);

        try {
            clientService.deleteClient(id.toString());
            log.info("Client deleted successfully");
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Client deletion failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting client", e);
            throw e;
        }
    }
}
