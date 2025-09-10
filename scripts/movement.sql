CREATE SCHEMA IF NOT EXISTS public;
-- ============================================
-- Script de creación de tabla 'movements'
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-08
-- ============================================

-- Crear secuencia para el ID autoincremental
CREATE SEQUENCE IF NOT EXISTS movements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Crear tabla movements
CREATE TABLE IF NOT EXISTS movements (
    -- Campos principales
    movement_id BIGSERIAL PRIMARY KEY,
    unique_key VARCHAR(64) NOT NULL UNIQUE,
    account_id BIGINT NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    movement_type VARCHAR(20) NOT NULL CHECK (movement_type IN ('DEBIT', 'CREDIT')),
    amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
    balance DECIMAL(19,2) NOT NULL,
    description VARCHAR(255),
    reference VARCHAR(100),

    -- Campos de auditoría
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints adicionales
    CONSTRAINT chk_unique_key_format CHECK (unique_key ~ '^[0-9a-zA-Z._:-]{8,64}$'),
    CONSTRAINT chk_unique_key_length CHECK (LENGTH(unique_key) >= 8 AND LENGTH(unique_key) <= 64)
);

-- Crear índices para optimizar consultas
CREATE INDEX IF NOT EXISTS idx_movements_unique_key ON movements(unique_key);
CREATE INDEX IF NOT EXISTS idx_movements_account_id ON movements(account_id);
CREATE INDEX IF NOT EXISTS idx_movements_occurred_at ON movements(occurred_at);
CREATE INDEX IF NOT EXISTS idx_movements_movement_type ON movements(movement_type);
CREATE INDEX IF NOT EXISTS idx_movements_created_at ON movements(created_at);
CREATE INDEX IF NOT EXISTS idx_movements_account_occurred ON movements(account_id, occurred_at);
CREATE INDEX IF NOT EXISTS idx_movements_account_type ON movements(account_id, movement_type);

-- Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_movements_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Crear trigger para actualizar updated_at
DROP TRIGGER IF EXISTS update_movements_updated_at ON movements;
CREATE TRIGGER update_movements_updated_at
    BEFORE UPDATE ON movements
    FOR EACH ROW
    EXECUTE FUNCTION update_movements_updated_at_column();

-- Comentarios en la tabla y columnas para documentación
COMMENT ON TABLE movements IS 'Tabla de movimientos de cuentas bancarias del sistema';
COMMENT ON COLUMN movements.movement_id IS 'Identificador único autoincrementable';
COMMENT ON COLUMN movements.unique_key IS 'Clave única para idempotencia (8-64 caracteres alfanuméricos)';
COMMENT ON COLUMN movements.account_id IS 'ID de la cuenta asociada';
COMMENT ON COLUMN movements.occurred_at IS 'Fecha y hora cuando ocurrió el movimiento';
COMMENT ON COLUMN movements.movement_type IS 'Tipo de movimiento: DEBIT o CREDIT';
COMMENT ON COLUMN movements.amount IS 'Monto del movimiento (debe ser mayor a 0)';
COMMENT ON COLUMN movements.balance IS 'Saldo de la cuenta después del movimiento';
COMMENT ON COLUMN movements.description IS 'Descripción opcional del movimiento';
COMMENT ON COLUMN movements.reference IS 'Referencia externa o ID de correlación';
COMMENT ON COLUMN movements.created_at IS 'Fecha y hora de creación del registro';
COMMENT ON COLUMN movements.updated_at IS 'Fecha y hora de última actualización';

-- Datos de ejemplo (opcional - descomenta si necesitas datos de prueba)

INSERT INTO movements (
    unique_key, account_id, occurred_at, movement_type, amount, balance, description, reference
) VALUES
(
    '3b1f1f9e-7d6c-4c0e-9a4b-5c5a3e2b9c10',
    1,
    '2025-09-08 10:00:00',
    'CREDIT',
    500.00,
    1500.00,
    'Depósito inicial',
    'DEP-001'
),
(
    'f2e4d6c8-1a3b-5f7e-9d1c-3e5f7a9b1d3f',
    1,
    '2025-09-08 15:30:00',
    'DEBIT',
    100.00,
    1400.00,
    'Retiro en cajero',
    'ATM-002'
);


-- Verificar la creación de la tabla
SELECT
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'movements'
ORDER BY ordinal_position;
