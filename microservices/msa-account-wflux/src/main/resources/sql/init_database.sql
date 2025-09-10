-- ============================================
-- Script de configuración inicial de base de datos
-- Microservicio: msa-account-wflux
-- Ejecutar como superusuario de PostgreSQL
-- ============================================

-- 1. Crear base de datos
DROP DATABASE IF EXISTS msa_account_db;
CREATE DATABASE msa_account_db
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 2. Crear usuario específico para el microservicio
DROP USER IF EXISTS msa_account_user;
CREATE USER msa_account_user WITH
    LOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    INHERIT
    NOREPLICATION
    CONNECTION LIMIT -1
    PASSWORD 'msa_account_password';

-- 3. Otorgar permisos al usuario
GRANT ALL PRIVILEGES ON DATABASE msa_account_db TO msa_account_user;

-- 4. Conectarse a la nueva base de datos
\c msa_account_db;

-- 5. Crear tabla accounts con el campo identification
CREATE TABLE IF NOT EXISTS accounts (
    -- Clave primaria
    account_id BIGSERIAL PRIMARY KEY,

    -- Campos principales
    account_number VARCHAR(50) NOT NULL UNIQUE,
    identification VARCHAR(20) NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS')),
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0),

    -- Campos de auditoría
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints adicionales
    CONSTRAINT chk_identification_format CHECK (identification ~ '^\d{8,12}$')
);

-- 6. Crear índices
CREATE INDEX IF NOT EXISTS idx_accounts_account_number ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_accounts_identification ON accounts(identification);
CREATE INDEX IF NOT EXISTS idx_accounts_account_type ON accounts(account_type);
CREATE INDEX IF NOT EXISTS idx_accounts_created_at ON accounts(created_at);

-- 7. Función para actualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 8. Trigger para updated_at
CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- 9. Otorgar permisos específicos al usuario
GRANT SELECT, INSERT, UPDATE, DELETE ON accounts TO msa_account_user;
GRANT USAGE, SELECT ON SEQUENCE accounts_account_id_seq TO msa_account_user;

-- 10. Datos de prueba
INSERT INTO accounts (account_number, identification, account_type, balance) VALUES
    ('ACC-001-SAVINGS', '12345678', 'SAVINGS', 1500.50),
    ('ACC-002-CHECKING', '87654321', 'CHECKING', 2300.75),
    ('ACC-003-BUSINESS', '11223344', 'BUSINESS', 5000.00),
    ('ACC-004-SAVINGS', '44332211', 'SAVINGS', 750.25)
ON CONFLICT (account_number) DO NOTHING;

-- 11. Verificar la creación
SELECT 'Tabla accounts creada exitosamente' AS status;
SELECT COUNT(*) AS total_records FROM accounts;
