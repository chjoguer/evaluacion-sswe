CREATE SCHEMA IF NOT EXISTS public;

-- Crear tabla accounts alineada con AccountEntity.java
CREATE TABLE IF NOT EXISTS accounts (
    -- Clave primaria (coincide con @Id en AccountEntity)
    account_id BIGSERIAL PRIMARY KEY,

    -- Número de cuenta único
    account_number VARCHAR(50) NOT NULL UNIQUE,
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
CREATE INDEX IF NOT EXISTS idx_accounts_account_type ON accounts(account_type);
CREATE INDEX IF NOT EXISTS idx_accounts_balance ON accounts(balance);
CREATE INDEX IF NOT EXISTS idx_accounts_created_at ON accounts(created_at);