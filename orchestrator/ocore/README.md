# OCore - Orchestrator Core Service

OCore es un servicio orquestador que implementa la arquitectura hexagonal para realizar llamadas a diferentes microservicios:
- msa-movement-wflux - Servicio de movimientos
- msa-account-wflux - Servicio de cuentas
- msa-client - Servicio de clientes

## Arquitectura

El proyecto sigue la arquitectura hexagonal (ports and adapters) con la siguiente estructura:

`
/project-root
  src/
      main/
          java/
              com/
                  example/
                      ocore/
                          application/         # Capa de aplicación
                             config/           # Configuración de aplicación
                             input/port/       # Puertos de entrada (casos de uso)
                             output/port/      # Puertos de salida (interfaces)
                             service/          # Servicios de aplicación
                          domain/              # Entidades del dominio
                          infrastructure/      # Capa de infraestructura
                             bean/             # Configuración de beans
                             config/           # Configuración de infraestructura
                             controller/       # Controladores REST
                             dto/              # DTOs para comunicación externa
                             impl/             # Implementaciones
                             output/
                                 adapter/      # Adaptadores de salida
`

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring WebFlux** (Programación reactiva)
- **Reactor Netty** (Cliente HTTP reactivo)
- **Resilience4j** (Circuit breaker)
- **Lombok** (Reducción de boilerplate)
- **Micrometer** (Métricas)

## Configuración

### Variables de entorno

El servicio se puede configurar mediante las siguientes propiedades en pplication.yml:

`yaml
services:
  movement:
    base-url: http://localhost:8081
    timeout: 5000
  account:
    base-url: http://localhost:8082
    timeout: 5000
  client:
    base-url: http://localhost:8083
    timeout: 5000
`

### Circuit Breaker

Se incluye configuración de circuit breaker para cada servicio externo para mejorar la resiliencia.

## Endpoints

### Movements
- POST /api/v1/movements - Crear movimiento
- GET /api/v1/movements/{id} - Obtener movimiento por ID
- GET /api/v1/movements - Obtener todos los movimientos (con filtros)
- PUT /api/v1/movements/{id} - Actualizar movimiento
- DELETE /api/v1/movements/{id} - Eliminar movimiento

### Accounts
- POST /api/v1/accounts - Crear cuenta
- GET /api/v1/accounts/{id} - Obtener cuenta por ID
- GET /api/v1/accounts/client/{clientId} - Obtener cuentas por ID de cliente
- PUT /api/v1/accounts/{id} - Actualizar cuenta
- DELETE /api/v1/accounts/{id} - Eliminar cuenta

### Clients
- POST /api/v1/clients - Crear cliente
- GET /api/v1/clients/{id} - Obtener cliente por ID
- GET /api/v1/clients - Obtener todos los clientes
- PUT /api/v1/clients/{id} - Actualizar cliente
- DELETE /api/v1/clients/{id} - Eliminar cliente

## Cómo ejecutar

### Desarrollo local
`ash
./gradlew bootRun
`

### Con Docker Compose
`ash
docker-compose up
`

### Construcción
`ash
./gradlew build
`

## Monitoreo

El servicio incluye endpoints de actuator para monitoreo:
- /actuator/health - Estado de salud
- /actuator/metrics - Métricas
- /actuator/prometheus - Métricas en formato Prometheus

## Principios de Arquitectura Hexagonal

1. **Separación de responsabilidades**: El dominio está aislado de los detalles de infraestructura
2. **Inversión de dependencias**: La aplicación depende de abstracciones, no de implementaciones concretas
3. **Testabilidad**: Cada capa puede ser testeada de forma independiente
4. **Flexibilidad**: Fácil cambio de adaptadores sin afectar la lógica de negocio
