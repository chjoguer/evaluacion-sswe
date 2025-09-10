-- ============================================
-- Script de creación de base de datos para MSA Movement
-- Base de datos: PostgreSQL
-- Fecha: 2025-09-08
-- ============================================

-- Crear la base de datos si no existe
-- (Nota: Este comando normalmente se ejecuta desde fuera de una transacción)
-- CREATE DATABASE msa_movement_db;

-- Conectarse a la base de datos
-- \c msa_movement_db;

-- Crear usuario específico para la aplicación si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'msa_movement_user') THEN
        CREATE USER msa_movement_user WITH PASSWORD 'msa_movement_password';
    END IF;
END
$$;

-- Otorgar permisos
GRANT CONNECT ON DATABASE msa_movement_db TO msa_movement_user;
GRANT USAGE ON SCHEMA public TO msa_movement_user;
GRANT CREATE ON SCHEMA public TO msa_movement_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO msa_movement_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO msa_movement_user;

-- Asegurar que el usuario tenga permisos en futuras tablas
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO msa_movement_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO msa_movement_user;

-- Crear extensiones útiles si no existen
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Mostrar información de la base de datos
SELECT current_database() as database_name, current_user as current_user, version() as postgresql_version;
