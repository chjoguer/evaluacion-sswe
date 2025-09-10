-- ============================================
-- Script de creación de tabla 'accounts'
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-10
-- ============================================

-- Crear tabla accounts
CREATE TABLE IF NOT EXISTS accounts (
    -- Primary Key
    account_id BIGSERIAL PRIMARY KEY,

    -- Campos principales
    account_number VARCHAR(50) NOT NULL UNIQUE,
    identification VARCHAR(20) NOT NULL,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('SAVINGS', 'CHECKING', 'BUSINESS')),
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,

    -- Campos de auditoría
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints adicionales
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_identification_format CHECK (identification ~ '^\d{8,12}$')
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
$$ language 'plpgsql';

-- Crear trigger para updated_at
DROP TRIGGER IF EXISTS update_accounts_updated_at ON accounts;
CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comentarios en la tabla y columnas para documentación
COMMENT ON TABLE accounts IS 'Tabla de cuentas bancarias del sistema';
COMMENT ON COLUMN accounts.account_id IS 'Identificador único autoincrementable';
COMMENT ON COLUMN accounts.account_number IS 'Número de cuenta único';
COMMENT ON COLUMN accounts.identification IS 'Número de identificación del titular de la cuenta';
COMMENT ON COLUMN accounts.account_type IS 'Tipo de cuenta: SAVINGS, CHECKING o BUSINESS';
COMMENT ON COLUMN accounts.balance IS 'Saldo actual de la cuenta';
COMMENT ON COLUMN accounts.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN accounts.updated_at IS 'Fecha y hora de última actualización';

-- Datos de ejemplo (opcional - descomenta si necesitas datos de prueba)
/*
INSERT INTO accounts (
    account_number, identification, account_type, balance
) VALUES
(
    'ACC-10000001',
    '12345678',
    'SAVINGS',
    1500.00
),
(
    'ACC-10000002',
    '87654321',
    'CHECKING',
    2500.00
);
*/

-- Verificar la creación de la tabla
SELECT
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'accounts'
ORDER BY ordinal_position;
