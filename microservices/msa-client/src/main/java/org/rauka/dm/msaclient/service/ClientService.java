package org.rauka.dm.msaclient.service;

import org.rauka.dm.msaclient.service.impl.ClientServiceImpl;
import org.rauka.dm.msaclient.service.models.Cliente;
import org.rauka.dm.msaclient.service.models.ClienteCreate;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByIdentification(String identification);
    Cliente createClient(ClienteCreate client);
    Cliente updateClient(ClienteCreate client, String identification);
    void deleteClient(String identification);
    boolean existsByIdentification(String identification);
}
