-- ============================================
-- Script de creación de base de datos y tabla 'accounts'
-- Microservicio: msa-account-wflux
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-10
-- ============================================

-- Crear base de datos (ejecutar como superusuario)
-- CREATE DATABASE msa_account_db;
-- CREATE USER msa_account_user WITH PASSWORD 'msa_account_password';
-- GRANT ALL PRIVILEGES ON DATABASE msa_account_db TO msa_account_user;

-- Conectarse a la base de datos msa_account_db
-- \c msa_account_db;

-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS public;

-- Crear tabla accounts alineada con AccountEntity.java
CREATE TABLE IF NOT EXISTS accounts (
    -- Clave primaria (coincide con @Id en AccountEntity)
    account_id BIGSERIAL PRIMARY KEY,

    -- Número de cuenta único
    account_number VARCHAR(50) NOT NULL UNIQUE,

    -- Identificación del titular de la cuenta
    identification VARCHAR(20) NOT NULL,

    -- Tipo de cuenta (enum: SAVINGS, CHECKING, BUSINESS)
    account_type VARCHAR(20) NOT NULL
        CHECK (account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS')),

    -- Saldo de la cuenta
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00
        CHECK (balance >= 0),

    -- Campos de auditoría
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_accounts_account_number ON accounts(account_number);
CREATE INDEX IF NOT EXISTS idx_accounts_identification ON accounts(identification);
CREATE INDEX IF NOT EXISTS idx_accounts_account_type ON accounts(account_type);
CREATE INDEX IF NOT EXISTS idx_accounts_created_at ON accounts(created_at);

-- Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear trigger para actualizar updated_at
DROP TRIGGER IF EXISTS update_accounts_updated_at ON accounts;
CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Agregar comentarios para documentación
COMMENT ON TABLE accounts IS 'Tabla de cuentas del microservicio msa-account-wflux';
COMMENT ON COLUMN accounts.account_id IS 'Identificador único de la cuenta (PK)';
COMMENT ON COLUMN accounts.account_number IS 'Número único de la cuenta';
COMMENT ON COLUMN accounts.identification IS 'Identificación del titular de la cuenta';
COMMENT ON COLUMN accounts.account_type IS 'Tipo de cuenta: SAVINGS, CHECKING, BUSINESS';
COMMENT ON COLUMN accounts.balance IS 'Saldo actual de la cuenta';
COMMENT ON COLUMN accounts.created_at IS 'Fecha de creación del registro';
COMMENT ON COLUMN accounts.updated_at IS 'Fecha de última actualización';

-- Datos de ejemplo para testing
INSERT INTO accounts (account_number, identification, account_type, balance) VALUES
    ('ACC-001-SAVINGS', '12345678', 'SAVINGS', 1500.50),
    ('ACC-002-CHECKING', '87654321', 'CHECKING', 2300.75),
    ('ACC-003-BUSINESS', '11223344', 'BUSINESS', 5000.00),
    ('ACC-004-SAVINGS', '44332211', 'SAVINGS', 750.25)
ON CONFLICT (account_number) DO NOTHING;

-- Verificar la estructura de la tabla
SELECT
    column_name,
    data_type,
    is_nullable,
    column_default,
    character_maximum_length
FROM information_schema.columns
WHERE table_name = 'accounts'
    AND table_schema = 'public'
ORDER BY ordinal_position;

-- Verificar los datos insertados
SELECT
    account_id,
    account_number,
    identification,
    account_type,
    balance,
    created_at,
    updated_at
FROM accounts
ORDER BY account_id;
