# MSA Account WebFlux - Microservicio de Cuentas

Microservicio reactivo para gestión de cuentas bancarias usando Spring WebFlux y R2DBC con PostgreSQL.

## 🚀 Configuración de Base de Datos

### Opción 1: Usando Docker Compose (Recomendado)

1. **Iniciar PostgreSQL con Docker:**
```bash
docker-compose up -d postgres
```

2. **Verificar que PostgreSQL esté funcionando:**
```bash
docker logs msa-account-postgres
```

3. **Opcional - Iniciar PgAdmin para administración:**
```bash
docker-compose up -d pgadmin
```
- Acceder a: http://localhost:8081
- Email: admin@msa-account.com
- Password: admin123

### Opción 2: PostgreSQL Local

1. **Instalar PostgreSQL localmente**

2. **Ejecutar el script de inicialización:**
```bash
psql -U postgres -f src/main/resources/sql/init_database.sql
```

3. **Conectarse a la nueva base de datos y crear las tablas:**
```bash
psql -U msa_account_user -d msa_account_db -f src/main/resources/sql/create_database_script.sql
```

## 🔧 Configuración del Proyecto

### Credenciales de Base de Datos
```yaml
# application.yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/msa_account_db
    username: msa_account_user
    password: msa_account_password
```

### Estructura de la Tabla
```sql
CREATE TABLE accounts (
    account_id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS')),
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

## 🏃‍♂️ Ejecutar el Microservicio

### 1. Compilar el proyecto:
```bash
./gradlew build
```

### 2. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

### 3. Verificar que esté funcionando:
- Health Check: http://localhost:8080/actuator/health
- OpenAPI UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## 📋 API Endpoints

### Gestión de Cuentas
- `GET /api/accounts` - Obtener todas las cuentas
- `POST /api/accounts` - Crear nueva cuenta
- `GET /api/accounts/{id}` - Obtener cuenta por ID
- `PUT /api/accounts/{id}` - Actualizar cuenta
- `DELETE /api/accounts/{id}` - Eliminar cuenta
- `GET /api/accounts/number/{accountNumber}` - Obtener cuenta por número
- `GET /api/accounts/type/{accountType}` - Obtener cuentas por tipo

### Ejemplo de JSON para crear cuenta:
```json
{
  "accountNumber": "ACC-005-SAVINGS",
  "accountType": "SAVINGS",
  "balance": 1000.00
}
```

## 🧪 Testing

### Ejecutar tests:
```bash
./gradlew test
```

### Tests con base de datos H2 (automático):
Los tests usan H2 en memoria automáticamente configurado.

## 🐳 Docker Commands

### Iniciar solo PostgreSQL:
```bash
docker-compose up -d postgres
```

### Ver logs de PostgreSQL:
```bash
docker logs -f msa-account-postgres
```

### Conectarse a PostgreSQL desde Docker:
```bash
docker exec -it msa-account-postgres psql -U msa_account_user -d msa_account_db
```

### Detener servicios:
```bash
docker-compose down
```

### Detener y eliminar volúmenes:
```bash
docker-compose down -v
```

## 📊 Monitoreo

### Actuator Endpoints disponibles:
- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas de la aplicación

## 🔍 Troubleshooting

### Error de conexión a base de datos:
1. Verificar que PostgreSQL esté ejecutándose
2. Verificar credenciales en `application.yaml`
3. Verificar que la base de datos `msa_account_db` exista

### Logs útiles:
```bash
# Ver logs de la aplicación
./gradlew bootRun --debug

# Ver logs de PostgreSQL
docker logs msa-account-postgres
```

### Reiniciar base de datos:
```bash
docker-compose down postgres
docker volume rm msa-account-wflux_postgres_data
docker-compose up -d postgres
```

## 🛠 Tecnologías Utilizadas

- **Spring Boot 3.5.5** - Framework principal
- **Spring WebFlux** - Programación reactiva
- **Spring Data R2DBC** - Acceso reactivo a datos
- **PostgreSQL** - Base de datos principal
- **H2** - Base de datos para testing
- **MapStruct** - Mapeo de DTOs
- **Lombok** - Reducción de boilerplate
- **OpenAPI Generator** - Generación automática de APIs
- **Docker** - Containerización
