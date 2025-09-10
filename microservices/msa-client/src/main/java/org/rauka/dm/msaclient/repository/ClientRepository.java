package org.rauka.dm.msaclient.repository;

import org.rauka.dm.msaclient.domain.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByIdentificacion(String identification);
    Optional<ClientEntity> findByClienteId(String clienteId);
    List<ClientEntity> findByEstado(Boolean estado);
    boolean existsByIdentificacion(String identification);
    boolean existsByClienteId(String clienteId);
}
