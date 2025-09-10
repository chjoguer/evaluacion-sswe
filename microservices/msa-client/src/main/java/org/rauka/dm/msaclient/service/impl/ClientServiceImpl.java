package org.rauka.dm.msaclient.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rauka.dm.msaclient.domain.ClientEntity;
import org.rauka.dm.msaclient.repository.ClientRepository;
import org.rauka.dm.msaclient.service.ClientService;
import org.rauka.dm.msaclient.service.mapper.ClientMapper;
import org.rauka.dm.msaclient.service.models.Cliente;
import org.rauka.dm.msaclient.service.models.ClienteCreate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public List<Cliente> findAll() {
        log.debug("Finding all clients");
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        log.debug("Finding client by id: {}", id);
        return clientRepository.findById(id)
                .map(clientMapper::toDTO);
    }

    @Override
    public Optional<Cliente> findByIdentification(String identification) {
        log.debug("Finding client by identification: {}", identification);
        return clientRepository.findByIdentificacion(identification)
                .map(clientMapper::toDTO);
    }

    @Override
    @Transactional
    public Cliente createClient(ClienteCreate client) {
        log.debug("Creating client with identification: {}");

        validateClientNotExists(client.getIdentification());
        ClientEntity savedClient = clientRepository.save(clientMapper.toEntity(client));
        log.info("Client created successfully with id: {}", savedClient.getId());

        return clientMapper.toDTO(savedClient);
    }

    @Override
    @Transactional
    public Cliente updateClient(ClienteCreate client,String identification) {
        log.debug("Updating client with identification: {}", identification);

        ClientEntity existingClient = findClientByIdentification(identification);
        clientMapper.updateEntityFromCreate(client, existingClient);
        ClientEntity updatedClient = clientRepository.save(existingClient);
        log.info("Client updated successfully with id: {}", updatedClient.getId());
        return clientMapper.toDTO(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(String identification) {
        log.debug("Deleting client with identification: {}", identification);

        ClientEntity client = findClientByIdentification(identification);
        client.setEstado(false);
        clientRepository.save(client);

        log.info("Client soft deleted successfully with id: {}", client.getId());
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return clientRepository.existsByIdentificacion(identification);
    }

    private void validateClientNotExists(String identification) {
        if (clientRepository.existsByIdentificacion(identification)) {
            throw new IllegalArgumentException("Client already exists with identification: " + identification);
        }
    }

    private ClientEntity findClientByIdentification(String identification) {
        return clientRepository.findByIdentificacion(identification)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with identification: " + identification));
    }

    private String generateUniqueClientId() {
        String clientId;
        do {
            clientId = "CLI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (clientRepository.existsByClienteId(clientId));
        return clientId;
    }
}
