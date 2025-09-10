package org.rauka.dm.msaclient.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rauka.dm.msaclient.domain.ClientEntity;
import org.rauka.dm.msaclient.repository.ClientRepository;
import org.rauka.dm.msaclient.service.mapper.ClientMapper;
import org.rauka.dm.msaclient.service.models.Cliente;
import org.rauka.dm.msaclient.service.models.ClienteCreate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientEntity clientEntity;
    private Cliente cliente;
    private ClienteCreate clienteCreate;

    @BeforeEach
    void setUp() {
        clientEntity = createClientEntity();
        cliente = createCliente();
        clienteCreate = createClienteCreate();
    }

    @Test
    void findAll_ShouldReturnListOfClients_WhenClientsExist() {
        List<ClientEntity> entities = List.of(clientEntity);
        List<Cliente> expectedClientes = List.of(cliente);

        when(clientRepository.findAll()).thenReturn(entities);
        when(clientMapper.toDTO(clientEntity)).thenReturn(cliente);

        List<Cliente> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedClientes, result);

        verify(clientRepository).findAll();
        verify(clientMapper).toDTO(clientEntity);
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenNoClientsExist() {
        when(clientRepository.findAll()).thenReturn(List.of());

        List<Cliente> result = clientService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clientRepository).findAll();
        verify(clientMapper, never()).toDTO(any());
    }

    @Test
    void findById_ShouldReturnClient_WhenClientExists() {
        Long id = 1L;
        when(clientRepository.findById(id)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDTO(clientEntity)).thenReturn(cliente);

        Optional<Cliente> result = clientService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());

        verify(clientRepository).findById(id);
        verify(clientMapper).toDTO(clientEntity);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenClientNotExists() {
        Long id = 1L;
        when(clientRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Cliente> result = clientService.findById(id);

        assertFalse(result.isPresent());

        verify(clientRepository).findById(id);
        verify(clientMapper, never()).toDTO(any());
    }

    @Test
    void findByIdentification_ShouldReturnClient_WhenClientExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.of(clientEntity));
        when(clientMapper.toDTO(clientEntity)).thenReturn(cliente);

        Optional<Cliente> result = clientService.findByIdentification(identification);

        assertTrue(result.isPresent());
        assertEquals(cliente, result.get());

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientMapper).toDTO(clientEntity);
    }

    @Test
    void findByIdentification_ShouldReturnEmpty_WhenClientNotExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.empty());

        Optional<Cliente> result = clientService.findByIdentification(identification);

        assertFalse(result.isPresent());

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientMapper, never()).toDTO(any());
    }

    @Test
    void createClient_ShouldCreateAndReturnClient_WhenValidDataProvided() {
        when(clientRepository.existsByIdentificacion(clienteCreate.getIdentification())).thenReturn(false);
        when(clientMapper.toEntity(clienteCreate)).thenReturn(clientEntity);
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
        when(clientMapper.toDTO(clientEntity)).thenReturn(cliente);

        Cliente result = clientService.createClient(clienteCreate);

        assertNotNull(result);
        assertEquals(cliente, result);

        verify(clientRepository).existsByIdentificacion(clienteCreate.getIdentification());
        verify(clientMapper).toEntity(clienteCreate);
        verify(clientRepository).save(clientEntity);
        verify(clientMapper).toDTO(clientEntity);
    }

    @Test
    void createClient_ShouldThrowException_WhenClientAlreadyExists() {
        when(clientRepository.existsByIdentificacion(clienteCreate.getIdentification())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.createClient(clienteCreate)
        );

        assertEquals("Client already exists with identification: " + clienteCreate.getIdentification(),
                     exception.getMessage());

        verify(clientRepository).existsByIdentificacion(clienteCreate.getIdentification());
        verify(clientMapper, never()).toEntity(any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void updateClient_ShouldUpdateAndReturnClient_WhenClientExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.of(clientEntity));
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
        when(clientMapper.toDTO(clientEntity)).thenReturn(cliente);

        Cliente result = clientService.updateClient(clienteCreate, identification);

        assertNotNull(result);
        assertEquals(cliente, result);

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientMapper).updateEntityFromCreate(clienteCreate, clientEntity);
        verify(clientRepository).save(clientEntity);
        verify(clientMapper).toDTO(clientEntity);
    }

    @Test
    void updateClient_ShouldThrowException_WhenClientNotExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.updateClient(clienteCreate, identification)
        );

        assertEquals("Client not found with identification: " + identification,
                     exception.getMessage());

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientMapper, never()).updateEntityFromCreate(any(), any());
        verify(clientRepository, never()).save(any());
    }

    @Test
    void deleteClient_ShouldSoftDeleteClient_WhenClientExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.of(clientEntity));
        when(clientRepository.save(clientEntity)).thenReturn(clientEntity);

        clientService.deleteClient(identification);

        assertFalse(clientEntity.getEstado());

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientRepository).save(clientEntity);
    }

    @Test
    void deleteClient_ShouldThrowException_WhenClientNotExists() {
        String identification = "1234567890";
        when(clientRepository.findByIdentificacion(identification)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> clientService.deleteClient(identification)
        );

        assertEquals("Client not found with identification: " + identification,
                     exception.getMessage());

        verify(clientRepository).findByIdentificacion(identification);
        verify(clientRepository, never()).save(any());
    }

    @Test
    void existsByIdentification_ShouldReturnTrue_WhenClientExists() {
        String identification = "1234567890";
        when(clientRepository.existsByIdentificacion(identification)).thenReturn(true);

        boolean result = clientService.existsByIdentification(identification);

        assertTrue(result);
        verify(clientRepository).existsByIdentificacion(identification);
    }

    @Test
    void existsByIdentification_ShouldReturnFalse_WhenClientNotExists() {
        String identification = "1234567890";
        when(clientRepository.existsByIdentificacion(identification)).thenReturn(false);

        boolean result = clientService.existsByIdentification(identification);

        assertFalse(result);
        verify(clientRepository).existsByIdentificacion(identification);
    }

    private ClientEntity createClientEntity() {
        ClientEntity entity = new ClientEntity();
        entity.setId(1L);
        entity.setClienteId("CLI-12345678");
        entity.setNombre("Juan Perez");
        entity.setDireccion("123 Main St");
        entity.setTelefono("0987654321");
        entity.setIdentificacion("1234567890");
        entity.setPassword("Password123!");
        entity.setEstado(true);
        entity.setGenero("M");
        entity.setEdad(30);
        return entity;
    }

    private Cliente createCliente() {
        Cliente client = new Cliente();
        client.setId(UUID.randomUUID());
        client.setFullName("Juan Perez");
        client.setDirection("123 Main St");
        client.setCellphone("0987654321");
        client.setIdentification("1234567890");
        client.setPassword("***");
        client.setStatus(true);
        client.setDocumentType(Cliente.DocumentTypeEnum.CEDULA);
        return client;
    }

    private ClienteCreate createClienteCreate() {
        ClienteCreate create = new ClienteCreate();
        create.setFullName("Juan Perez");
        create.setDirection("123 Main St");
        create.setCellphone("0987654321");
        create.setIdentification("1234567890");
        create.setPassword("Password123!");
        create.setStatus(true);
        return create;
    }
}
