package org.rauka.dm.msapdfgenerator.service;

import org.rauka.dm.msapdfgenerator.dto.ClienteDTO;
import reactor.core.publisher.Mono;

public interface ClientService {

    Mono<ClienteDTO> getClienteByIdentificacion(String identificacion);
}
